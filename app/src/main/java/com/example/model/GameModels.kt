package com.example.model

enum class GameMode(val title: String, val description: String) {
    CLASSIC(
        title = "Classic",
        description = "Endless puzzle experience. Clear lines and beat your high score!"
    ),
    TIME_ATTACK(
        title = "Time Attack",
        description = "120-second rush! Maximize points and combos before the clock runs out."
    ),
    DAILY_CHALLENGE(
        title = "Daily Challenge",
        description = "Clear 15 lines today to dominate and claim 50 gold coins!"
    )
}

data class ScorePopup(
    val id: Long,
    val text: String,
    val startX: Float,
    val startY: Float,
    val color: androidx.compose.ui.graphics.Color,
    val createdAt: Long = System.currentTimeMillis()
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float = 1.0f,
    val color: androidx.compose.ui.graphics.Color,
    val size: Float,
    val maxLife: Float = 30f,
    var currentLife: Float = 0f
)
