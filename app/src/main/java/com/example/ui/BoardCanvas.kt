package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.BoardTheme
import com.example.model.Board
import com.example.model.CubeColor
import com.example.model.Particle
import com.example.model.Piece

@Composable
fun BoardCanvas(
    board: Board,
    theme: BoardTheme,
    previewPiece: Piece?,
    previewColRow: Pair<Int, Int>?,
    isValidPreview: Boolean,
    particles: List<Particle>,
    screenShakeOffset: Offset,
    onCellTapped: (col: Int, row: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = theme.boardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, theme.boardBorder),
        modifier = modifier
            .aspectRatio(1f)
            .padding(screenShakeOffset.x.dp, screenShakeOffset.y.dp, 0.dp, 0.dp)
            .testTag("game_board_card")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .testTag("game_board_canvas")
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val cellSize = size.width / 8f
                            val col = (offset.x / cellSize).toInt().coerceIn(0, 7)
                            val row = (offset.y / cellSize).toInt().coerceIn(0, 7)
                            onCellTapped(col, row)
                        }
                    }
            ) {
                val boardSize = size.minDimension
                val cellSize = boardSize / 8f
                val cellPad = cellSize * 0.075f
                val cornerRadius = CornerRadius(cellSize * 0.22f, cellSize * 0.22f)

                // 1. Draw empty cells (3D recessed arcade slots)
                for (r in 0 until 8) {
                    for (c in 0 until 8) {
                        val left = c * cellSize + cellPad
                        val top = r * cellSize + cellPad
                        val w = cellSize - cellPad * 2
                        val h = cellSize - cellPad * 2

                        // Recessed slot background with subtle depth gradient
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF030712).copy(alpha = 0.7f),
                                    theme.emptyCellBackground
                                ),
                                startY = top,
                                endY = top + h
                            ),
                            topLeft = Offset(left, top),
                            size = Size(w, h),
                            cornerRadius = cornerRadius
                        )

                        // Inset upper shadow for depth
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.45f),
                            topLeft = Offset(left, top),
                            size = Size(w, h * 0.3f),
                            cornerRadius = CornerRadius(cornerRadius.x, cornerRadius.y * 0.6f)
                        )

                        // Crisp inner glowing border
                        drawRoundRect(
                            color = theme.emptyCellBorder.copy(alpha = 0.85f),
                            topLeft = Offset(left, top),
                            size = Size(w, h),
                            cornerRadius = cornerRadius,
                            style = Stroke(width = 1.2f)
                        )
                    }
                }

                // 2. Draw placed blocks (3D glossy beveled cubes)
                for (r in 0 until 8) {
                    for (c in 0 until 8) {
                        val cell = board.getCell(c, r)
                        if (cell.isOccupied && cell.color != null) {
                            val left = c * cellSize + cellPad
                            val top = r * cellSize + cellPad
                            val w = cellSize - cellPad * 2
                            val h = cellSize - cellPad * 2

                            drawGlossyCube(
                                color = cell.color,
                                topLeft = Offset(left, top),
                                size = Size(w, h),
                                cornerRadius = cornerRadius,
                                alpha = 1.0f
                            )
                        }
                    }
                }

                // 3. Draw ghost / placement preview
                if (previewPiece != null && previewColRow != null) {
                    val (startCol, startRow) = previewColRow
                    val cubeColor = previewPiece.color

                    for ((dx, dy) in previewPiece.shape.coordinates) {
                        val c = startCol + dx
                        val r = startRow + dy
                        if (c in 0 until 8 && r in 0 until 8) {
                            val left = c * cellSize + cellPad
                            val top = r * cellSize + cellPad
                            val w = cellSize - cellPad * 2
                            val h = cellSize - cellPad * 2

                            if (isValidPreview) {
                                // Draw translucent preview cube
                                drawGlossyCube(
                                    color = cubeColor,
                                    topLeft = Offset(left, top),
                                    size = Size(w, h),
                                    cornerRadius = cornerRadius,
                                    alpha = pulseAlpha * 0.85f
                                )
                                // Glowing pulsating cyber outline
                                drawRoundRect(
                                    color = Color.White.copy(alpha = pulseAlpha),
                                    topLeft = Offset(left, top),
                                    size = Size(w, h),
                                    cornerRadius = cornerRadius,
                                    style = Stroke(width = 3f)
                                )
                                drawRoundRect(
                                    color = cubeColor.glowColor.copy(alpha = 0.8f),
                                    topLeft = Offset(left - 1.5f, top - 1.5f),
                                    size = Size(w + 3f, h + 3f),
                                    cornerRadius = cornerRadius,
                                    style = Stroke(width = 1.5f)
                                )
                            } else {
                                // Red error highlight with glowing warning border
                                drawRoundRect(
                                    color = Color(0xFFEF4444).copy(alpha = 0.35f),
                                    topLeft = Offset(left, top),
                                    size = Size(w, h),
                                    cornerRadius = cornerRadius
                                )
                                drawRoundRect(
                                    color = Color(0xFFF87171),
                                    topLeft = Offset(left, top),
                                    size = Size(w, h),
                                    cornerRadius = cornerRadius,
                                    style = Stroke(width = 2.5f)
                                )
                            }
                        }
                    }
                }

                // 4. Draw burst particles on line clears
                for (p in particles) {
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha),
                        radius = p.size,
                        center = Offset(p.x, p.y)
                    )
                    // Inner bright core
                    drawCircle(
                        color = Color.White.copy(alpha = (p.alpha * 0.9f).coerceIn(0f, 1f)),
                        radius = p.size * 0.45f,
                        center = Offset(p.x, p.y)
                    )
                }
            }
        }
    }
}

/**
 * Renders a premium 3D beveled, glossy, tactile game cube.
 */
fun DrawScope.drawGlossyCube(
    color: CubeColor,
    topLeft: Offset,
    size: Size,
    cornerRadius: CornerRadius,
    alpha: Float = 1.0f
) {
    val w = size.width
    val h = size.height

    // 1. Ambient drop shadow (makes cube pop out from recessed grid)
    val shadowOffset = h * 0.06f
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.55f * alpha),
        topLeft = Offset(topLeft.x, topLeft.y + shadowOffset),
        size = Size(w, h),
        cornerRadius = cornerRadius
    )

    // 2. Base beveled body (smooth linear gradient from light to rich dark shadow)
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                color.lightHighlight.copy(alpha = alpha),
                color.primary.copy(alpha = alpha),
                color.darkShadow.copy(alpha = alpha)
            ),
            startY = topLeft.y,
            endY = topLeft.y + h
        ),
        topLeft = topLeft,
        size = size,
        cornerRadius = cornerRadius
    )

    // 3. Dark lower bevel shadow (giving true 3D bottom bevel)
    val bevelH = h * 0.22f
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.4f * alpha)
            ),
            startY = topLeft.y + h - bevelH,
            endY = topLeft.y + h
        ),
        topLeft = Offset(topLeft.x, topLeft.y + h - bevelH),
        size = Size(w, bevelH),
        cornerRadius = CornerRadius(cornerRadius.x, cornerRadius.y)
    )

    // 4. Lighter upper edge highlight (simulating overhead studio light)
    val topEdgeH = h * 0.18f
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.5f * alpha),
                Color.Transparent
            ),
            startY = topLeft.y,
            endY = topLeft.y + topEdgeH
        ),
        topLeft = topLeft,
        size = Size(w, topEdgeH),
        cornerRadius = CornerRadius(cornerRadius.x, cornerRadius.y * 0.5f)
    )

    // 5. Glossy glass reflection sheen (angled top-center shield reflection)
    val sheenPadX = w * 0.12f
    val sheenPadY = h * 0.10f
    val sheenW = w - sheenPadX * 2
    val sheenH = h * 0.38f
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.55f * alpha),
                Color.White.copy(alpha = 0.08f * alpha)
            ),
            startY = topLeft.y + sheenPadY,
            endY = topLeft.y + sheenPadY + sheenH
        ),
        topLeft = Offset(topLeft.x + sheenPadX, topLeft.y + sheenPadY),
        size = Size(sheenW, sheenH),
        cornerRadius = CornerRadius(cornerRadius.x * 0.75f, cornerRadius.y * 0.75f)
    )

    // 6. Crisp outer neon rim (defines edges on budget screens)
    drawRoundRect(
        color = color.lightHighlight.copy(alpha = 0.7f * alpha),
        topLeft = topLeft,
        size = size,
        cornerRadius = cornerRadius,
        style = Stroke(width = 1.5f)
    )

    // 7. Specular sparkle highlight in top-left
    drawCircle(
        color = Color.White.copy(alpha = 0.85f * alpha),
        radius = w * 0.065f,
        center = Offset(topLeft.x + w * 0.24f, topLeft.y + h * 0.24f)
    )
}
