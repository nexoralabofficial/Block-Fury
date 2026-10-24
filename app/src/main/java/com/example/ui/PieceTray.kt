package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.data.BoardTheme
import com.example.model.Piece
import kotlin.math.roundToInt

@Composable
fun PieceTray(
    pieces: List<Piece?>,
    selectedPieceIndex: Int?,
    theme: BoardTheme,
    onPieceSelected: (index: Int) -> Unit,
    onDragStart: (index: Int, initialRootOffset: Offset) -> Unit,
    onDragMove: (delta: Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "selectedPulse")
    val selectedGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "selectedGlowAlpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(118.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag("piece_tray_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 3) {
            val piece = pieces.getOrNull(i)
            val isSelected = selectedPieceIndex == i

            PieceTraySlot(
                piece = piece,
                index = i,
                isSelected = isSelected,
                glowAlpha = selectedGlowAlpha,
                theme = theme,
                onSelect = { onPieceSelected(i) },
                onDragStart = { rootPos -> onDragStart(i, rootPos) },
                onDragMove = onDragMove,
                onDragEnd = onDragEnd,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
fun PieceTraySlot(
    piece: Piece?,
    index: Int,
    isSelected: Boolean,
    glowAlpha: Float,
    theme: BoardTheme,
    onSelect: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var slotRootPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (isSelected) listOf(
                        Color(0xFF1E3A8A).copy(alpha = 0.55f),
                        Color(0xFF0F172A).copy(alpha = 0.85f)
                    ) else listOf(
                        Color(0xFF0F172A).copy(alpha = 0.6f),
                        Color(0xFF020617).copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                brush = if (isSelected) Brush.horizontalGradient(
                    listOf(
                        Color(0xFF38BDF8).copy(alpha = glowAlpha),
                        Color(0xFFFACC15).copy(alpha = glowAlpha)
                    )
                ) else Brush.linearGradient(
                    listOf(
                        theme.boardBorder.copy(alpha = 0.45f),
                        Color(0xFF334155).copy(alpha = 0.25f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .onGloballyPositioned { coordinates ->
                slotRootPosition = coordinates.positionInRoot()
            }
            .clickable {
                if (piece != null) {
                    onSelect()
                }
            }
            .pointerInput(piece) {
                if (piece != null) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            onDragStart(slotRootPosition + offset)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onDragMove(dragAmount)
                        },
                        onDragEnd = {
                            onDragEnd()
                        },
                        onDragCancel = {
                            onDragEnd()
                        }
                    )
                }
            }
            .testTag("tray_slot_$index"),
        contentAlignment = Alignment.Center
    ) {
        if (piece != null) {
            val scale = if (isSelected) 1.08f else 1.0f
            PieceMiniCanvas(
                piece = piece,
                modifier = Modifier
                    .fillMaxSize(0.86f)
                    .scale(scale)
            )
        }
    }
}

@Composable
fun PieceMiniCanvas(
    piece: Piece,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val shape = piece.shape
        val maxDim = maxOf(shape.width, shape.height, 3)
        val cellSize = size.minDimension / maxDim
        val cellPad = cellSize * 0.08f
        val cornerRadius = CornerRadius(cellSize * 0.22f, cellSize * 0.22f)

        // Center piece within canvas
        val totalWidth = shape.width * cellSize
        val totalHeight = shape.height * cellSize
        val offsetX = (size.width - totalWidth) / 2f
        val offsetY = (size.height - totalHeight) / 2f

        for ((dx, dy) in shape.coordinates) {
            val left = offsetX + dx * cellSize + cellPad
            val top = offsetY + dy * cellSize + cellPad
            val w = cellSize - cellPad * 2
            val h = cellSize - cellPad * 2

            drawGlossyCube(
                color = piece.color,
                topLeft = Offset(left, top),
                size = Size(w, h),
                cornerRadius = cornerRadius,
                alpha = 1.0f
            )
        }
    }
}

@Composable
fun DraggedPieceOverlay(
    piece: Piece,
    currentTouchPos: Offset,
    boardCellSize: Float,
    modifier: Modifier = Modifier
) {
    // Lift piece above thumb so finger never obscures blocks
    val shape = piece.shape
    val pieceW = shape.width * boardCellSize
    val pieceH = shape.height * boardCellSize

    val left = currentTouchPos.x - pieceW * 0.5f
    val top = currentTouchPos.y - pieceH - 45f

    Box(
        modifier = modifier
            .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
            .size((shape.width * boardCellSize).dp, (shape.height * boardCellSize).dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellPad = boardCellSize * 0.08f
            val cornerRadius = CornerRadius(boardCellSize * 0.22f, boardCellSize * 0.22f)

            for ((dx, dy) in shape.coordinates) {
                val cLeft = dx * boardCellSize + cellPad
                val cTop = dy * boardCellSize + cellPad
                val w = boardCellSize - cellPad * 2
                val h = boardCellSize - cellPad * 2

                drawGlossyCube(
                    color = piece.color,
                    topLeft = Offset(cLeft, cTop),
                    size = Size(w, h),
                    cornerRadius = cornerRadius,
                    alpha = 0.96f
                )
            }
        }
    }
}
