package com.example.model

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Vibrant block cube colors with glossy gradient tones.
 */
enum class CubeColor(
    val primary: Color,
    val lightHighlight: Color,
    val darkShadow: Color,
    val glowColor: Color,
    val displayName: String
) {
    RED(
        primary = Color(0xFFEF4444),
        lightHighlight = Color(0xFFFCA5A5),
        darkShadow = Color(0xFF991B1B),
        glowColor = Color(0xFFFF6B6B),
        displayName = "Crimson Ruby"
    ),
    ORANGE(
        primary = Color(0xFFF97316),
        lightHighlight = Color(0xFFFED7AA),
        darkShadow = Color(0xFFC2410C),
        glowColor = Color(0xFFFF9E42),
        displayName = "Solar Blaze"
    ),
    YELLOW(
        primary = Color(0xFFFACC15),
        lightHighlight = Color(0xFFFEF08A),
        darkShadow = Color(0xFFA16207),
        glowColor = Color(0xFFFFE66D),
        displayName = "Electric Gold"
    ),
    GREEN(
        primary = Color(0xFF10B981),
        lightHighlight = Color(0xFFA7F3D0),
        darkShadow = Color(0xFF047857),
        glowColor = Color(0xFF34D399),
        displayName = "Neon Emerald"
    ),
    CYAN(
        primary = Color(0xFF06B6D4),
        lightHighlight = Color(0xFFA5F3FC),
        darkShadow = Color(0xFF0E7490),
        glowColor = Color(0xFF22D3EE),
        displayName = "Cyber Cyan"
    ),
    BLUE(
        primary = Color(0xFF3B82F6),
        lightHighlight = Color(0xFFBFDBFE),
        darkShadow = Color(0xFF1D4ED8),
        glowColor = Color(0xFF60A5FA),
        displayName = "Hyper Blue"
    ),
    PURPLE(
        primary = Color(0xFF8B5CF6),
        lightHighlight = Color(0xFFDDD6FE),
        darkShadow = Color(0xFF5B21B6),
        glowColor = Color(0xFFA78BFA),
        displayName = "Cosmic Violet"
    ),
    MAGENTA(
        primary = Color(0xFFEC4899),
        lightHighlight = Color(0xFFFBCFE8),
        darkShadow = Color(0xFF9D174D),
        glowColor = Color(0xFFF472B6),
        displayName = "Laser Pink"
    )
}

/**
 * Shape definitions for block pieces.
 * Coordinates are relative (x, y) where x is column offset and y is row offset.
 */
data class PieceShape(
    val name: String,
    val coordinates: List<Pair<Int, Int>>
) {
    val width: Int = coordinates.maxOf { it.first } + 1
    val height: Int = coordinates.maxOf { it.second } + 1
    val blockCount: Int = coordinates.size
}

data class Piece(
    val id: String,
    val shape: PieceShape,
    val color: CubeColor
)

object PieceTemplates {
    // 1-Block
    val DOT = PieceShape("DOT", listOf(0 to 0))

    // 2-Blocks
    val LINE_2_H = PieceShape("LINE_2_H", listOf(0 to 0, 1 to 0))
    val LINE_2_V = PieceShape("LINE_2_V", listOf(0 to 0, 0 to 1))

    // 3-Blocks
    val LINE_3_H = PieceShape("LINE_3_H", listOf(0 to 0, 1 to 0, 2 to 0))
    val LINE_3_V = PieceShape("LINE_3_V", listOf(0 to 0, 0 to 1, 0 to 2))
    val CORNER_3_TL = PieceShape("CORNER_3_TL", listOf(0 to 0, 1 to 0, 0 to 1))
    val CORNER_3_TR = PieceShape("CORNER_3_TR", listOf(0 to 0, 1 to 0, 1 to 1))
    val CORNER_3_BL = PieceShape("CORNER_3_BL", listOf(0 to 0, 0 to 1, 1 to 1))
    val CORNER_3_BR = PieceShape("CORNER_3_BR", listOf(1 to 0, 0 to 1, 1 to 1))

    // 4-Blocks
    val SQUARE_2X2 = PieceShape("SQUARE_2X2", listOf(0 to 0, 1 to 0, 0 to 1, 1 to 1))
    val LINE_4_H = PieceShape("LINE_4_H", listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0))
    val LINE_4_V = PieceShape("LINE_4_V", listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3))
    val T_UP = PieceShape("T_UP", listOf(1 to 0, 0 to 1, 1 to 1, 2 to 1))
    val T_DOWN = PieceShape("T_DOWN", listOf(0 to 0, 1 to 0, 2 to 0, 1 to 1))
    val T_LEFT = PieceShape("T_LEFT", listOf(1 to 0, 0 to 1, 1 to 1, 1 to 2))
    val T_RIGHT = PieceShape("T_RIGHT", listOf(0 to 0, 0 to 1, 1 to 1, 0 to 2))
    val L_4_1 = PieceShape("L_4_1", listOf(0 to 0, 0 to 1, 0 to 2, 1 to 2))
    val L_4_2 = PieceShape("L_4_2", listOf(1 to 0, 1 to 1, 0 to 2, 1 to 2))
    val L_4_3 = PieceShape("L_4_3", listOf(0 to 0, 1 to 0, 2 to 0, 0 to 1))
    val L_4_4 = PieceShape("L_4_4", listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1))
    val Z_H = PieceShape("Z_H", listOf(0 to 0, 1 to 0, 1 to 1, 2 to 1))
    val S_H = PieceShape("S_H", listOf(1 to 0, 2 to 0, 0 to 1, 1 to 1))
    val Z_V = PieceShape("Z_V", listOf(0 to 1, 1 to 0, 1 to 1, 0 to 2))
    val S_V = PieceShape("S_V", listOf(0 to 0, 0 to 1, 1 to 1, 1 to 2))

    // 5-Blocks
    val LINE_5_H = PieceShape("LINE_5_H", listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0))
    val LINE_5_V = PieceShape("LINE_5_V", listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4))
    val PLUS_5 = PieceShape("PLUS_5", listOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 1 to 2))
    val BIG_L_1 = PieceShape("BIG_L_1", listOf(0 to 0, 0 to 1, 0 to 2, 1 to 2, 2 to 2))
    val BIG_L_2 = PieceShape("BIG_L_2", listOf(2 to 0, 2 to 1, 0 to 2, 1 to 2, 2 to 2))
    val BIG_L_3 = PieceShape("BIG_L_3", listOf(0 to 0, 1 to 0, 2 to 0, 0 to 1, 0 to 2))
    val BIG_L_4 = PieceShape("BIG_L_4", listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 2 to 2))

    // 9-Blocks (Big 3x3)
    val SQUARE_3X3 = PieceShape(
        "SQUARE_3X3",
        listOf(
            0 to 0, 1 to 0, 2 to 0,
            0 to 1, 1 to 1, 2 to 1,
            0 to 2, 1 to 2, 2 to 2
        )
    )

    val ALL_SHAPES = listOf(
        DOT,
        LINE_2_H, LINE_2_V,
        LINE_3_H, LINE_3_V,
        CORNER_3_TL, CORNER_3_TR, CORNER_3_BL, CORNER_3_BR,
        SQUARE_2X2,
        LINE_4_H, LINE_4_V,
        T_UP, T_DOWN, T_LEFT, T_RIGHT,
        L_4_1, L_4_2, L_4_3, L_4_4,
        Z_H, S_H, Z_V, S_V,
        LINE_5_H, LINE_5_V,
        PLUS_5,
        BIG_L_1, BIG_L_2, BIG_L_3, BIG_L_4,
        SQUARE_3X3
    )

    /**
     * Generates a balanced trio of pieces.
     * Prevents giving 3 giant pieces at the same time so players have fair strategic options.
     */
    fun generateTrio(random: Random = Random): List<Piece> {
        val result = mutableListOf<Piece>()
        val shapes = ALL_SHAPES
        val colors = CubeColor.values()

        // 1st piece: small to medium (1 to 4 blocks)
        val smallPool = shapes.filter { it.blockCount <= 4 }
        val p1Shape = smallPool[random.nextInt(smallPool.size)]
        result.add(Piece("piece_${System.nanoTime()}_0", p1Shape, colors[random.nextInt(colors.size)]))

        // 2nd piece: any shape
        val p2Shape = shapes[random.nextInt(shapes.size)]
        result.add(Piece("piece_${System.nanoTime()}_1", p2Shape, colors[random.nextInt(colors.size)]))

        // 3rd piece: if 2nd piece was large (>4), bias towards manageable piece
        val p3Pool = if (p2Shape.blockCount > 4) {
            shapes.filter { it.blockCount <= 4 }
        } else {
            shapes
        }
        val p3Shape = p3Pool[random.nextInt(p3Pool.size)]
        result.add(Piece("piece_${System.nanoTime()}_2", p3Shape, colors[random.nextInt(colors.size)]))

        return result
    }
}
