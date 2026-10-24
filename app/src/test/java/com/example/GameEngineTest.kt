package com.example

import com.example.model.Board
import com.example.model.BoardCell
import com.example.model.CubeColor
import com.example.model.Piece
import com.example.model.PieceShape
import com.example.model.PieceTemplates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    @Test
    fun testBoardInitialization() {
        val board = Board(8)
        for (r in 0 until 8) {
            for (c in 0 until 8) {
                assertFalse(board.getCell(c, r).isOccupied)
            }
        }
    }

    @Test
    fun testValidAndInvalidPlacement() {
        val board = Board(8)
        val piece2x2 = Piece("p1", PieceTemplates.SQUARE_2X2, CubeColor.RED)

        // Valid placement
        assertTrue(board.canPlacePiece(piece2x2, 0, 0))
        assertTrue(board.placePiece(piece2x2, 0, 0))

        // Check cells occupied
        assertTrue(board.getCell(0, 0).isOccupied)
        assertTrue(board.getCell(1, 0).isOccupied)
        assertTrue(board.getCell(0, 1).isOccupied)
        assertTrue(board.getCell(1, 1).isOccupied)
        assertFalse(board.getCell(2, 0).isOccupied)

        // Overlapping placement rejected
        assertFalse(board.canPlacePiece(piece2x2, 0, 0))
        assertFalse(board.canPlacePiece(piece2x2, 1, 1))

        // Out of bounds placement rejected
        assertFalse(board.canPlacePiece(piece2x2, 7, 7)) // extends to col 8, row 8
        assertFalse(board.canPlacePiece(piece2x2, -1, 0))
    }

    @Test
    fun testHorizontalLineClear() {
        val board = Board(8)
        // Fill row 3 entirely except last column
        for (c in 0 until 7) {
            board.setCell(c, 3, BoardCell(color = CubeColor.BLUE))
        }
        val (initialRows, _) = board.findFullLines()
        assertTrue(initialRows.isEmpty())

        // Place 1-block in col 7 row 3
        val dot = Piece("dot", PieceTemplates.DOT, CubeColor.YELLOW)
        assertTrue(board.placePiece(dot, 7, 3))

        val (fullRows, fullCols) = board.findFullLines()
        assertEquals(1, fullRows.size)
        assertEquals(3, fullRows[0])
        assertTrue(fullCols.isEmpty())

        val cleared = board.clearLines(fullRows, fullCols)
        assertEquals(8, cleared.size)
        for (c in 0 until 8) {
            assertFalse(board.getCell(c, 3).isOccupied)
        }
    }

    @Test
    fun testSimultaneousRowAndColumnClear() {
        val board = Board(8)
        // Fill entire row 2
        for (c in 0 until 8) {
            board.setCell(c, 2, BoardCell(color = CubeColor.GREEN))
        }
        // Fill entire col 5
        for (r in 0 until 8) {
            board.setCell(5, r, BoardCell(color = CubeColor.CYAN))
        }

        val (rows, cols) = board.findFullLines()
        assertEquals(listOf(2), rows)
        assertEquals(listOf(5), cols)

        // 8 + 8 - 1 (intersection at 5, 2) = 15 distinct cells
        val cleared = board.clearLines(rows, cols)
        assertEquals(15, cleared.size)

        // Row 2 and col 5 are now empty
        for (c in 0 until 8) assertFalse(board.getCell(c, 2).isOccupied)
        for (r in 0 until 8) assertFalse(board.getCell(5, r).isOccupied)
    }

    @Test
    fun testGameOverDetection() {
        val board = Board(8)
        // Fill board completely
        for (r in 0 until 8) {
            for (c in 0 until 8) {
                board.setCell(c, r, BoardCell(color = CubeColor.RED))
            }
        }

        val dot = Piece("dot", PieceTemplates.DOT, CubeColor.YELLOW)
        val line3 = Piece("line3", PieceTemplates.LINE_3_H, CubeColor.BLUE)

        // Neither can fit on a full board
        assertFalse(board.canAnyPieceFit(listOf(dot, line3)))

        // Leave one empty spot at (4, 4)
        board.setCell(4, 4, BoardCell())
        // Now dot can fit!
        assertTrue(board.canAnyPieceFit(listOf(dot)))
        // But line3 cannot fit
        assertFalse(board.canAnyPieceFit(listOf(line3)))
    }

    @Test
    fun testPieceTemplatesValidity() {
        val trio = PieceTemplates.generateTrio()
        assertEquals(3, trio.size)
        for (p in trio) {
            assertTrue(p.shape.blockCount in 1..9)
            assertTrue(p.shape.width in 1..8)
            assertTrue(p.shape.height in 1..8)
            for ((dx, dy) in p.shape.coordinates) {
                assertTrue(dx >= 0)
                assertTrue(dy >= 0)
            }
        }
    }

    @Test
    fun testSmartLinkUrlExactness() {
        org.junit.Assert.assertEquals(
            "https://www.profitableratecpmnetwork.com/cbni9m1hk2?key=1691fbbe4463e0e3e65f7e27606f80e7",
            com.example.util.SmartLinkHelper.SMARTLINK_URL
        )
    }
}
