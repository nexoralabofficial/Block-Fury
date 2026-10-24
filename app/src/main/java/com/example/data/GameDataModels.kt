package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.model.CubeColor

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val coinReward: Int,
    val iconName: String
)

object AchievementList {
    val ALL = listOf(
        Achievement(
            id = "first_clear",
            title = "First Blood",
            description = "Clear your first row or column.",
            coinReward = 20,
            iconName = "flash"
        ),
        Achievement(
            id = "double_trouble",
            title = "Double Smash",
            description = "Clear 2 lines in a single move.",
            coinReward = 30,
            iconName = "layers"
        ),
        Achievement(
            id = "multi_master",
            title = "Arcade Fury",
            description = "Clear 3 or more lines simultaneously!",
            coinReward = 60,
            iconName = "stars"
        ),
        Achievement(
            id = "fury_unleashed",
            title = "Fury Mode",
            description = "Achieve a 3x consecutive combo.",
            coinReward = 50,
            iconName = "fire"
        ),
        Achievement(
            id = "score_1000",
            title = "Millennium Club",
            description = "Reach 1,000 points in any mode.",
            coinReward = 40,
            iconName = "trophy"
        ),
        Achievement(
            id = "score_3000",
            title = "Cube Grandmaster",
            description = "Reach 3,000 points in Classic mode.",
            coinReward = 100,
            iconName = "crown"
        ),
        Achievement(
            id = "time_rusher",
            title = "Speed Demon",
            description = "Score 1,500+ points in Time Attack.",
            coinReward = 80,
            iconName = "timer"
        ),
        Achievement(
            id = "daily_challenger",
            title = "Daily Dominator",
            description = "Complete a Daily Challenge objective.",
            coinReward = 50,
            iconName = "calendar"
        )
    )
}

data class BoardTheme(
    val id: String,
    val name: String,
    val cost: Int,
    val boardBackground: Color,
    val boardBorder: Color,
    val emptyCellBackground: Color,
    val emptyCellBorder: Color,
    val appBackgroundStart: Color,
    val appBackgroundEnd: Color,
    val previewColors: List<Color>
)

object ThemePresets {
    val NEON_ARCADE = BoardTheme(
        id = "NEON_ARCADE",
        name = "Neon Arcade",
        cost = 0,
        boardBackground = Color(0xFF0A122E),
        boardBorder = Color(0xFF1E3A8A),
        emptyCellBackground = Color(0xFF0F1A42),
        emptyCellBorder = Color(0xFF182A6B),
        appBackgroundStart = Color(0xFF060B1E),
        appBackgroundEnd = Color(0xFF0D1B3E),
        previewColors = listOf(CubeColor.RED.primary, CubeColor.YELLOW.primary, CubeColor.CYAN.primary, CubeColor.GREEN.primary)
    )

    val COSMIC_DEEP = BoardTheme(
        id = "COSMIC_DEEP",
        name = "Cosmic Deep",
        cost = 150,
        boardBackground = Color(0xFF08071E),
        boardBorder = Color(0xFF4C1D95),
        emptyCellBackground = Color(0xFF120E38),
        emptyCellBorder = Color(0xFF261D6E),
        appBackgroundStart = Color(0xFF04030F),
        appBackgroundEnd = Color(0xFF130D3A),
        previewColors = listOf(CubeColor.PURPLE.primary, CubeColor.MAGENTA.primary, CubeColor.CYAN.primary, CubeColor.BLUE.primary)
    )

    val SUNSET_BLAZE = BoardTheme(
        id = "SUNSET_BLAZE",
        name = "Sunset Blaze",
        cost = 250,
        boardBackground = Color(0xFF1F0B0B),
        boardBorder = Color(0xFF991B1B),
        emptyCellBackground = Color(0xFF331212),
        emptyCellBorder = Color(0xFF571D1D),
        appBackgroundStart = Color(0xFF120505),
        appBackgroundEnd = Color(0xFF2E0F0F),
        previewColors = listOf(CubeColor.ORANGE.primary, CubeColor.YELLOW.primary, CubeColor.RED.primary, CubeColor.MAGENTA.primary)
    )

    val EMERALD_MINE = BoardTheme(
        id = "EMERALD_MINE",
        name = "Emerald Mine",
        cost = 350,
        boardBackground = Color(0xFF061A14),
        boardBorder = Color(0xFF065F46),
        emptyCellBackground = Color(0xFF0B2E24),
        emptyCellBorder = Color(0xFF114F3E),
        appBackgroundStart = Color(0xFF030D0A),
        appBackgroundEnd = Color(0xFF0A2920),
        previewColors = listOf(CubeColor.GREEN.primary, CubeColor.CYAN.primary, CubeColor.YELLOW.primary, CubeColor.BLUE.primary)
    )

    val ALL = listOf(NEON_ARCADE, COSMIC_DEEP, SUNSET_BLAZE, EMERALD_MINE)

    fun getById(id: String): BoardTheme {
        return ALL.find { it.id == id } ?: NEON_ARCADE
    }
}
