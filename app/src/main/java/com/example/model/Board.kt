package com.example.model

data class BoardCell(
    val color: CubeColor? = null,
    val isClearing: Boolean = false,
    val clearProgress: Float = 0f
) {
    val isOccupied: Boolean get() = color != null
}

class Board(val size: Int = 8) {
    private val grid: Array<Array<BoardCell>> = Array(size) {
        Array(size) { BoardCell() }
    }

    fun getCell(col: Int, row: Int): BoardCell {
        if (col !in 0 until size || row !in 0 until size) return BoardCell()
        return grid[row][col]
    }

    fun setCell(col: Int, row: Int, cell: BoardCell) {
        if (col in 0 until size && row in 0 until size) {
            grid[row][col] = cell
        }
    }

    fun clear() {
        for (r in 0 until size) {
            for (c in 0 until size) {
                grid[r][c] = BoardCell()
            }
        }
    }

    fun copy(): Board {
        val newBoard = Board(size)
        for (r in 0 until size) {
            for (c in 0 until size) {
                newBoard.setCell(c, r, grid[r][c])
            }
        }
        return newBoard
    }

    /**
     * Checks if a piece can be placed at (startCol, startRow).
     */
    fun canPlacePiece(piece: Piece, startCol: Int, startRow: Int): Boolean {
        for ((dx, dy) in piece.shape.coordinates) {
            val c = startCol + dx
            val r = startRow + dy
            if (c !in 0 until size || r !in 0 until size) {
                return false
            }
            if (grid[r][c].isOccupied) {
                return false
            }
        }
        return true
    }

    /**
     * Places a piece at (startCol, startRow).
     * Assumes canPlacePiece has passed.
     */
    fun placePiece(piece: Piece, startCol: Int, startRow: Int): Boolean {
        if (!canPlacePiece(piece, startCol, startRow)) return false
        for ((dx, dy) in piece.shape.coordinates) {
            val c = startCol + dx
            val r = startRow + dy
            grid[r][c] = BoardCell(color = piece.color)
        }
        return true
    }

    /**
     * Finds full horizontal rows and full vertical columns.
     */
    fun findFullLines(): Pair<List<Int>, List<Int>> {
        val fullRows = mutableListOf<Int>()
        val fullCols = mutableListOf<Int>()

        // Check horizontal rows
        for (r in 0 until size) {
            var full = true
            for (c in 0 until size) {
                if (!grid[r][c].isOccupied) {
                    full = false
                    break
                }
            }
            if (full) fullRows.add(r)
        }

        // Check vertical columns
        for (c in 0 until size) {
            var full = true
            for (r in 0 until size) {
                if (!grid[r][c].isOccupied) {
                    full = false
                    break
                }
            }
            if (full) fullCols.add(c)
        }

        return Pair(fullRows, fullCols)
    }

    /**
     * Clears specified rows and columns simultaneously.
     * Returns the distinct cells that were cleared (for particle effects).
     */
    fun clearLines(rows: List<Int>, cols: List<Int>): List<Pair<Int, Int>> {
        val clearedCells = mutableListOf<Pair<Int, Int>>()
        val clearedSet = mutableSetOf<Pair<Int, Int>>()

        for (r in rows) {
            for (c in 0 until size) {
                if (clearedSet.add(c to r)) {
                    clearedCells.add(c to r)
                }
                grid[r][c] = BoardCell()
            }
        }

        for (c in cols) {
            for (r in 0 until size) {
                if (clearedSet.add(c to r)) {
                    clearedCells.add(c to r)
                }
                grid[r][c] = BoardCell()
            }
        }

        return clearedCells
    }

    /**
     * Tests if this piece can fit anywhere on the board.
     */
    fun canPieceFitAnywhere(piece: Piece): Boolean {
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (canPlacePiece(piece, c, r)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Tests if ANY piece in the list can fit anywhere on the board.
     */
    fun canAnyPieceFit(pieces: List<Piece?>): Boolean {
        val activePieces = pieces.filterNotNull()
        if (activePieces.isEmpty()) return true
        for (p in activePieces) {
            if (canPieceFitAnywhere(p)) {
                return true
            }
        }
        return false
    }
}
