package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.GameAudioManager
import com.example.data.AchievementList
import com.example.data.GamePreferences
import com.example.data.ThemePresets
import com.example.model.Board
import com.example.model.CubeColor
import com.example.model.GameMode
import com.example.model.Particle
import com.example.model.Piece
import com.example.model.PieceTemplates
import com.example.model.ScorePopup
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameUiState(
    val gameMode: GameMode = GameMode.CLASSIC,
    val board: Board = Board(8),
    val trayPieces: List<Piece?> = listOf(null, null, null),
    val selectedPieceIndex: Int? = null,
    val draggedPieceIndex: Int? = null,
    val dragOffset: Offset = Offset.Zero,
    val hoverColRow: Pair<Int, Int>? = null,
    val isValidHover: Boolean = false,
    val score: Int = 0,
    val bestScore: Int = 0,
    val combo: Int = 0,
    val highestCombo: Int = 0,
    val linesCleared: Int = 0,
    val blocksPlaced: Int = 0,
    val timeRemaining: Int = 120, // Time attack
    val dailyTargetLines: Int = 15,
    val dailyLinesCleared: Int = 0,
    val dailyCompleted: Boolean = false,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
    val isNewRecord: Boolean = false,
    val isFuryMode: Boolean = false,
    val furyActiveSeconds: Int = 0,
    val comboBannerText: String? = null,
    val screenShakeIntensity: Float = 0f,
    val scorePopups: List<ScorePopup> = emptyList(),
    val particles: List<Particle> = emptyList(),
    val coinsEarnedThisGame: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val preferences = GamePreferences(application.applicationContext)
    val audioManager = GameAudioManager(application.applicationContext)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var furyTimerJob: Job? = null

    init {
        // Sync audio settings
        audioManager.sfxEnabled = preferences.sfxEnabled.value
        audioManager.musicEnabled = preferences.musicEnabled.value
        audioManager.sfxVolume = preferences.sfxVolume.value
        audioManager.musicVolume = preferences.musicVolume.value

        startNewGame(GameMode.CLASSIC)
    }

    fun startNewGame(mode: GameMode) {
        timerJob?.cancel()
        furyTimerJob?.cancel()

        val best = when (mode) {
            GameMode.CLASSIC -> preferences.highScoreClassic.value
            GameMode.TIME_ATTACK -> preferences.highScoreTimeAttack.value
            GameMode.DAILY_CHALLENGE -> preferences.highScoreClassic.value
        }

        val dailyCompleted = preferences.isDailyCompletedToday()

        val seed = if (mode == GameMode.DAILY_CHALLENGE) {
            preferences.getTodayDateString().hashCode().toLong()
        } else {
            System.currentTimeMillis()
        }
        val random = Random(seed)
        val initialTrio = PieceTemplates.generateTrio(random)

        _uiState.value = GameUiState(
            gameMode = mode,
            board = Board(8),
            trayPieces = initialTrio,
            bestScore = best,
            dailyCompleted = dailyCompleted,
            timeRemaining = if (mode == GameMode.TIME_ATTACK) 120 else 0
        )

        audioManager.playGameplayMusic(isFury = false)

        if (mode == GameMode.TIME_ATTACK) {
            startTimeAttackTimer()
        }
    }

    private fun startTimeAttackTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0 && !_uiState.value.isGameOver) {
                delay(1000)
                if (!_uiState.value.isPaused) {
                    val remaining = _uiState.value.timeRemaining - 1
                    _uiState.value = _uiState.value.copy(timeRemaining = remaining)
                    if (remaining == 0) {
                        handleGameOver()
                    }
                }
            }
        }
    }

    fun selectPiece(index: Int) {
        val piece = _uiState.value.trayPieces.getOrNull(index) ?: return
        if (_uiState.value.isGameOver || _uiState.value.isPaused) return

        if (_uiState.value.selectedPieceIndex == index) {
            _uiState.value = _uiState.value.copy(selectedPieceIndex = null)
        } else {
            _uiState.value = _uiState.value.copy(selectedPieceIndex = index)
            audioManager.playSound(GameAudioManager.SoundEffect.PIECE_SELECT)
            triggerHaptic(HapticType.LIGHT)
        }
    }

    fun onDragStart(index: Int, offset: Offset) {
        val piece = _uiState.value.trayPieces.getOrNull(index) ?: return
        if (_uiState.value.isGameOver || _uiState.value.isPaused) return

        _uiState.value = _uiState.value.copy(
            draggedPieceIndex = index,
            dragOffset = offset,
            selectedPieceIndex = index
        )
        audioManager.playSound(GameAudioManager.SoundEffect.PIECE_SELECT)
        triggerHaptic(HapticType.LIGHT)
    }

    fun onDragMove(offset: Offset, hoverColRow: Pair<Int, Int>?) {
        val index = _uiState.value.draggedPieceIndex ?: return
        val piece = _uiState.value.trayPieces.getOrNull(index) ?: return

        val isValid = if (hoverColRow != null) {
            _uiState.value.board.canPlacePiece(piece, hoverColRow.first, hoverColRow.second)
        } else {
            false
        }

        _uiState.value = _uiState.value.copy(
            dragOffset = offset,
            hoverColRow = hoverColRow,
            isValidHover = isValid
        )
    }

    fun onDragEnd(dropSuccessful: Boolean) {
        val index = _uiState.value.draggedPieceIndex
        val hover = _uiState.value.hoverColRow
        val isValid = _uiState.value.isValidHover

        if (index != null && hover != null && isValid && dropSuccessful) {
            placePiece(index, hover.first, hover.second)
        } else if (index != null && hover != null && !isValid) {
            audioManager.playSound(GameAudioManager.SoundEffect.INVALID_PLACE)
            triggerHaptic(HapticType.ERROR)
        }

        _uiState.value = _uiState.value.copy(
            draggedPieceIndex = null,
            dragOffset = Offset.Zero,
            hoverColRow = null,
            isValidHover = false
        )
    }

    fun placePiece(pieceIndex: Int, startCol: Int, startRow: Int): Boolean {
        val state = _uiState.value
        if (state.isGameOver || state.isPaused) return false
        val piece = state.trayPieces.getOrNull(pieceIndex) ?: return false

        val board = state.board
        if (!board.canPlacePiece(piece, startCol, startRow)) {
            audioManager.playSound(GameAudioManager.SoundEffect.INVALID_PLACE)
            triggerHaptic(HapticType.ERROR)
            return false
        }

        // Place on board
        board.placePiece(piece, startCol, startRow)
        val newTray = state.trayPieces.toMutableList()
        newTray[pieceIndex] = null

        val blockPoints = piece.shape.blockCount * 10
        val placedBlocksCount = state.blocksPlaced + piece.shape.blockCount
        var newScore = state.score + blockPoints

        // Check line clears
        val (fullRows, fullCols) = board.findFullLines()
        val totalLines = fullRows.size + fullCols.size

        var newCombo = state.combo
        var highestCombo = state.highestCombo
        var isFury = state.isFuryMode
        var comboText: String? = null
        var linesClearedCount = state.linesCleared
        var dailyLines = state.dailyLinesCleared
        var earnedCoins = state.coinsEarnedThisGame

        if (totalLines > 0) {
            newCombo++
            if (newCombo > highestCombo) highestCombo = newCombo

            linesClearedCount += totalLines
            dailyLines += totalLines

            // Line clear points
            val linePoints = when (totalLines) {
                1 -> 100
                2 -> 300
                3 -> 600
                else -> 1000 + (totalLines - 4) * 400
            }
            val comboBonus = newCombo * 50
            newScore += linePoints + comboBonus

            // Clear cells and spawn particles
            val clearedCells = board.clearLines(fullRows, fullCols)
            spawnParticles(clearedCells, piece.color.primary)

            // Coins earned
            var coinsToAdd = totalLines
            if (newCombo >= 3) coinsToAdd += 5
            preferences.addCoins(coinsToAdd)
            earnedCoins += coinsToAdd
            audioManager.playSound(GameAudioManager.SoundEffect.COIN_REWARD)

            // Audio & vibration based on clear count
            when {
                totalLines >= 3 -> {
                    audioManager.playSound(GameAudioManager.SoundEffect.CLEAR_MULTI)
                    comboText = if (newCombo >= 3) "FURY MODE! x$newCombo" else "ARCADE BURST! +${linePoints + comboBonus}"
                    triggerHaptic(HapticType.HEAVY)
                    triggerScreenShake(14f)
                }
                totalLines == 2 -> {
                    audioManager.playSound(GameAudioManager.SoundEffect.CLEAR_DOUBLE)
                    comboText = if (newCombo >= 3) "SUPER COMBO! x$newCombo" else "DOUBLE CLEAR! +${linePoints + comboBonus}"
                    triggerHaptic(HapticType.MEDIUM)
                    triggerScreenShake(8f)
                }
                else -> {
                    audioManager.playSound(GameAudioManager.SoundEffect.CLEAR_SINGLE)
                    comboText = if (newCombo >= 3) "FURY COMBO! x$newCombo" else "NICE CLEAR! +${linePoints + comboBonus}"
                    triggerHaptic(HapticType.LIGHT)
                    triggerScreenShake(4f)
                }
            }

            // Fury mode check
            if (newCombo >= 3) {
                isFury = true
                audioManager.playSound(GameAudioManager.SoundEffect.COMBO_FURY)
                audioManager.playGameplayMusic(isFury = true)
                startFuryTimer()
                preferences.unlockAchievement("fury_unleashed")
            } else {
                audioManager.playSound(GameAudioManager.SoundEffect.COMBO_UP)
            }

            // Achievements
            preferences.unlockAchievement("first_clear")
            if (totalLines >= 2) preferences.unlockAchievement("double_trouble")
            if (totalLines >= 3) preferences.unlockAchievement("multi_master")

            // Floating score popup
            addScorePopup("+$linePoints", startCol, startRow, piece.color.glowColor)

        } else {
            // No lines cleared -> reset combo
            newCombo = 0
            isFury = false
            audioManager.playSound(GameAudioManager.SoundEffect.BLOCK_PLACE)
            triggerHaptic(HapticType.LIGHT)
            addScorePopup("+$blockPoints", startCol, startRow, Color.White)
        }

        // Daily Challenge completion check
        var dailyCompleted = state.dailyCompleted
        if (state.gameMode == GameMode.DAILY_CHALLENGE && !dailyCompleted && dailyLines >= state.dailyTargetLines) {
            dailyCompleted = true
            preferences.markDailyCompleted()
            preferences.addCoins(50)
            preferences.unlockAchievement("daily_challenger")
            audioManager.playSound(GameAudioManager.SoundEffect.CHALLENGE_COMPLETE)
        }

        // Check score achievements
        if (newScore >= 1000) preferences.unlockAchievement("score_1000")
        if (newScore >= 3000 && state.gameMode == GameMode.CLASSIC) preferences.unlockAchievement("score_3000")
        if (newScore >= 1500 && state.gameMode == GameMode.TIME_ATTACK) preferences.unlockAchievement("time_rusher")

        // Refill tray if all 3 are used
        var finalTray = newTray.toList()
        if (finalTray.all { it == null }) {
            finalTray = PieceTemplates.generateTrio()
        }

        // Check Game Over: can any remaining piece fit on the board?
        val gameOver = !board.canAnyPieceFit(finalTray)

        // Best score updates
        val isNewRecord = when (state.gameMode) {
            GameMode.CLASSIC -> preferences.updateClassicScore(newScore)
            GameMode.TIME_ATTACK -> preferences.updateTimeAttackScore(newScore)
            GameMode.DAILY_CHALLENGE -> preferences.updateClassicScore(newScore)
        }

        if (isNewRecord && !state.isNewRecord) {
            audioManager.playSound(GameAudioManager.SoundEffect.HIGH_SCORE)
        }

        _uiState.value = state.copy(
            board = board,
            trayPieces = finalTray,
            selectedPieceIndex = null,
            score = newScore,
            bestScore = maxOf(state.bestScore, newScore),
            combo = newCombo,
            highestCombo = highestCombo,
            linesCleared = linesClearedCount,
            blocksPlaced = placedBlocksCount,
            dailyLinesCleared = dailyLines,
            dailyCompleted = dailyCompleted,
            isFuryMode = isFury,
            comboBannerText = comboText,
            isNewRecord = isNewRecord || state.isNewRecord,
            coinsEarnedThisGame = earnedCoins,
            isGameOver = gameOver
        )

        if (gameOver) {
            handleGameOver()
        }

        return true
    }

    private fun handleGameOver() {
        timerJob?.cancel()
        furyTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(isGameOver = true)
        audioManager.playSound(GameAudioManager.SoundEffect.GAME_OVER)
        triggerHaptic(HapticType.HEAVY)
    }

    private fun startFuryTimer() {
        furyTimerJob?.cancel()
        furyTimerJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(furyActiveSeconds = 10)
            while (_uiState.value.furyActiveSeconds > 0) {
                delay(1000)
                val sec = _uiState.value.furyActiveSeconds - 1
                _uiState.value = _uiState.value.copy(furyActiveSeconds = sec)
            }
            if (_uiState.value.isFuryMode) {
                _uiState.value = _uiState.value.copy(isFuryMode = false)
                audioManager.playGameplayMusic(isFury = false)
            }
        }
    }

    private fun triggerScreenShake(intensity: Float) {
        if (preferences.reducedMotion.value) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(screenShakeIntensity = intensity)
            delay(200)
            _uiState.value = _uiState.value.copy(screenShakeIntensity = 0f)
        }
    }

    private fun spawnParticles(clearedCells: List<Pair<Int, Int>>, color: Color) {
        if (preferences.reducedMotion.value) return
        val newParticles = mutableListOf<Particle>()
        val random = Random
        for ((c, r) in clearedCells) {
            val cellCenterX = c * 50f + 25f
            val cellCenterY = r * 50f + 25f
            for (i in 0 until 5) {
                val angle = random.nextDouble() * 2 * Math.PI
                val speed = random.nextFloat() * 4f + 2f
                newParticles.add(
                    Particle(
                        x = cellCenterX,
                        y = cellCenterY,
                        vx = (Math.cos(angle) * speed).toFloat(),
                        vy = (Math.sin(angle) * speed).toFloat(),
                        color = color,
                        size = random.nextFloat() * 6f + 4f
                    )
                )
            }
        }
        _uiState.value = _uiState.value.copy(particles = _uiState.value.particles + newParticles)
    }

    private fun addScorePopup(text: String, col: Int, row: Int, color: Color) {
        val popup = ScorePopup(
            id = System.nanoTime(),
            text = text,
            startX = col.toFloat(),
            startY = row.toFloat(),
            color = color
        )
        _uiState.value = _uiState.value.copy(scorePopups = _uiState.value.scorePopups + popup)
        viewModelScope.launch {
            delay(1200)
            _uiState.value = _uiState.value.copy(
                scorePopups = _uiState.value.scorePopups.filter { it.id != popup.id }
            )
        }
    }

    fun pauseGame() {
        if (_uiState.value.isGameOver) return
        _uiState.value = _uiState.value.copy(isPaused = true)
        audioManager.playSound(GameAudioManager.SoundEffect.PAUSE)
        audioManager.stopMusic()
    }

    fun resumeGame() {
        if (_uiState.value.isGameOver) return
        _uiState.value = _uiState.value.copy(isPaused = false)
        audioManager.playSound(GameAudioManager.SoundEffect.RESUME)
        audioManager.playGameplayMusic(isFury = _uiState.value.isFuryMode)
    }

    fun restartGame() {
        startNewGame(_uiState.value.gameMode)
    }

    fun clearComboBanner() {
        _uiState.value = _uiState.value.copy(comboBannerText = null)
    }

    enum class HapticType {
        LIGHT, MEDIUM, HEAVY, ERROR
    }

    private fun triggerHaptic(type: HapticType) {
        if (!preferences.hapticsEnabled.value || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    HapticType.LIGHT -> VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE)
                    HapticType.MEDIUM -> VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                    HapticType.HEAVY -> VibrationEffect.createWaveform(longArrayOf(0, 30, 40, 60), -1)
                    HapticType.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 40, 50, 40), -1)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    HapticType.LIGHT -> vibrator.vibrate(15)
                    HapticType.MEDIUM -> vibrator.vibrate(35)
                    HapticType.HEAVY -> vibrator.vibrate(80)
                    HapticType.ERROR -> vibrator.vibrate(60)
                }
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        furyTimerJob?.cancel()
        audioManager.release()
    }
}
