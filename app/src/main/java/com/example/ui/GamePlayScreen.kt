package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameAudioManager
import com.example.data.ThemePresets
import com.example.model.GameMode
import com.example.util.SmartLinkHelper
import com.example.viewmodel.GameViewModel

@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val preferences = viewModel.preferences
    val audioManager = viewModel.audioManager

    val selectedThemeId by preferences.selectedThemeId.collectAsState()
    val sfxEnabled by preferences.sfxEnabled.collectAsState()
    val musicEnabled by preferences.musicEnabled.collectAsState()
    val coins by preferences.coins.collectAsState()
    val reducedMotion by preferences.reducedMotion.collectAsState()

    val currentTheme = ThemePresets.getById(selectedThemeId)

    // Back handler: pauses the game cleanly
    BackHandler {
        if (!uiState.isGameOver && !uiState.isPaused) {
            viewModel.pauseGame()
        } else if (uiState.isPaused || uiState.isGameOver) {
            onNavigateHome()
        }
    }

    var boardBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    var currentTouchPos by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    // Screen Shake Offset
    val shakeOffset = remember(uiState.screenShakeIntensity) {
        if (uiState.screenShakeIntensity > 0f) {
            Offset(
                (Math.random() * uiState.screenShakeIntensity - uiState.screenShakeIntensity / 2).toFloat(),
                (Math.random() * uiState.screenShakeIntensity - uiState.screenShakeIntensity / 2).toFloat()
            )
        } else {
            Offset.Zero
        }
    }

    AtmosphericBackground(theme = currentTheme, reducedMotion = reducedMotion) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("game_play_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 520.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. TOP SECTION: Premium Glassmorphism HUD
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.85f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E3A8A)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("score_display_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Controls: Pause & Quick Sound Toggle
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.pauseGame() },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFF1E293B), CircleShape)
                                        .border(1.dp, Color(0xFF334155), CircleShape)
                                        .testTag("btn_pause_game")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        contentDescription = "Pause Game",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = {
                                        val next = !sfxEnabled
                                        preferences.setSfxEnabled(next)
                                        audioManager.sfxEnabled = next
                                        if (next) audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(if (sfxEnabled) Color(0xFF1E3A8A) else Color(0xFF1E293B), CircleShape)
                                        .border(1.dp, Color(0xFF334155), CircleShape)
                                        .testTag("btn_quick_sfx")
                                ) {
                                    Icon(
                                        imageVector = if (sfxEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                        contentDescription = "Toggle SFX",
                                        tint = if (sfxEnabled) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Center: Prominent Score Display
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SCORE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "${uiState.score}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF38BDF8)
                                )
                            }

                            // Right: Best Score & Coins Badge
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color(0xFFFACC15),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${uiState.bestScore}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFACC15)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("🪙", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$coins",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFBBF24)
                                    )
                                }
                            }
                        }
                    }

                    // Mode Banner (Time Attack progress or Daily Challenge)
                    if (uiState.gameMode == GameMode.TIME_ATTACK) {
                        val isCritical = uiState.timeRemaining <= 20
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isCritical) Color(0xFF7F1D1D).copy(alpha = 0.9f) else Color(0xFF0F172A).copy(alpha = 0.8f),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(1.dp, if (isCritical) Color(0xFFEF4444) else Color(0xFFF97316).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (isCritical) Color(0xFFF87171) else Color(0xFFF97316),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TIME ATTACK: ${uiState.timeRemaining}s",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCritical) Color.White else Color(0xFFF97316)
                                )
                            }
                            LinearProgressIndicator(
                                progress = { (uiState.timeRemaining / 120f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .width(90.dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (isCritical) Color(0xFFEF4444) else Color(0xFFF97316),
                                trackColor = Color(0xFF334155)
                            )
                        }
                    } else if (uiState.gameMode == GameMode.DAILY_CHALLENGE) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A).copy(alpha = 0.85f), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFFACC15).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.dailyCompleted) "★ DAILY GOAL COMPLETE! (+50 🪙)" else "DAILY GOAL: ${uiState.dailyLinesCleared}/${uiState.dailyTargetLines} LINES CLEARED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.dailyCompleted) Color(0xFF34D399) else Color(0xFFFACC15)
                            )
                        }
                    }

                    // 2. Combo / Fury Mode Announcement Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .padding(top = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isFuryMode) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.horizontalGradient(listOf(Color(0xFFDC2626), Color(0xFFF97316), Color(0xFFFACC15))),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .border(1.5.dp, Color.White, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 18.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "⚡ FURY MODE! (${uiState.furyActiveSeconds}s) ⚡",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        } else if (uiState.comboBannerText != null) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.horizontalGradient(listOf(Color(0xFF1E40AF), Color(0xFF3B82F6))),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .border(1.dp, Color(0xFF60A5FA), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = uiState.comboBannerText!!,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // 3. MIDDLE SECTION: The 8x8 Board as Centerpiece
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .onGloballyPositioned { coordinates ->
                            boardBounds = coordinates.boundsInRoot()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val previewPiece = uiState.draggedPieceIndex?.let { uiState.trayPieces.getOrNull(it) }
                        ?: uiState.selectedPieceIndex?.let { uiState.trayPieces.getOrNull(it) }

                    BoardCanvas(
                        board = uiState.board,
                        theme = currentTheme,
                        previewPiece = previewPiece,
                        previewColRow = uiState.hoverColRow,
                        isValidPreview = uiState.isValidHover,
                        particles = uiState.particles,
                        screenShakeOffset = shakeOffset,
                        onCellTapped = { col, row ->
                            val selectedIdx = uiState.selectedPieceIndex
                            if (selectedIdx != null) {
                                viewModel.placePiece(selectedIdx, col, row)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Floating Score Labels
                    for (popup in uiState.scorePopups) {
                        val cellSize = boardBounds.width / 8f
                        val posX = popup.startX * cellSize + cellSize * 0.5f
                        val posY = popup.startY * cellSize
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = posX.dp, top = posY.dp)
                        ) {
                            Text(
                                text = popup.text,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = popup.color
                            )
                        }
                    }
                }

                // 4. BOTTOM SECTION: Refined Piece Tray
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    PieceTray(
                        pieces = uiState.trayPieces,
                        selectedPieceIndex = uiState.selectedPieceIndex,
                        theme = currentTheme,
                        onPieceSelected = { index ->
                            viewModel.selectPiece(index)
                        },
                        onDragStart = { index, initialRootOffset ->
                            isDragging = true
                            currentTouchPos = initialRootOffset
                            viewModel.onDragStart(index, initialRootOffset)
                        },
                        onDragMove = { delta ->
                            currentTouchPos += delta
                            val draggedIdx = uiState.draggedPieceIndex
                            val piece = draggedIdx?.let { uiState.trayPieces.getOrNull(it) }
                            if (piece != null && boardBounds.width > 0f) {
                                val cellSize = boardBounds.width / 8f
                                val pieceW = piece.shape.width * cellSize
                                val pieceH = piece.shape.height * cellSize
                                val pieceLeft = currentTouchPos.x - pieceW * 0.5f
                                val pieceTop = currentTouchPos.y - pieceH - 45f

                                if (boardBounds.contains(Offset(pieceLeft + cellSize * 0.5f, pieceTop + cellSize * 0.5f))) {
                                    val col = ((pieceLeft - boardBounds.left + cellSize * 0.5f) / cellSize).toInt().coerceIn(0, 8 - piece.shape.width)
                                    val row = ((pieceTop - boardBounds.top + cellSize * 0.5f) / cellSize).toInt().coerceIn(0, 8 - piece.shape.height)
                                    viewModel.onDragMove(currentTouchPos, col to row)
                                } else {
                                    viewModel.onDragMove(currentTouchPos, null)
                                }
                            }
                        },
                        onDragEnd = {
                            isDragging = false
                            viewModel.onDragEnd(dropSuccessful = true)
                        }
                    )
                }
            }

            // Dragged piece floating overlay
            val draggedIdx = uiState.draggedPieceIndex
            val draggedPiece = draggedIdx?.let { uiState.trayPieces.getOrNull(it) }
            if (isDragging && draggedPiece != null && boardBounds.width > 0f) {
                val cellSize = boardBounds.width / 8f
                DraggedPieceOverlay(
                    piece = draggedPiece,
                    currentTouchPos = currentTouchPos,
                    boardCellSize = cellSize
                )
            }

            // Pause Dialog
            if (uiState.isPaused) {
                PauseDialog(
                    onResume = { viewModel.resumeGame() },
                    onRestart = { viewModel.restartGame() },
                    onHome = onNavigateHome,
                    sfxEnabled = sfxEnabled,
                    musicEnabled = musicEnabled,
                    onToggleSfx = {
                        preferences.setSfxEnabled(it)
                        audioManager.sfxEnabled = it
                    },
                    onToggleMusic = {
                        preferences.setMusicEnabled(it)
                        audioManager.musicEnabled = it
                    },
                    onOpenSpecialOffers = {
                        SmartLinkHelper.openSmartLink(context)
                    }
                )
            }

            // Game Over Dialog
            if (uiState.isGameOver) {
                GameOverDialog(
                    score = uiState.score,
                    bestScore = uiState.bestScore,
                    isNewRecord = uiState.isNewRecord,
                    linesCleared = uiState.linesCleared,
                    highestCombo = uiState.highestCombo,
                    blocksPlaced = uiState.blocksPlaced,
                    coinsEarned = uiState.coinsEarnedThisGame,
                    onPlayAgain = { viewModel.restartGame() },
                    onHome = onNavigateHome
                )
            }
        }
    }
}
