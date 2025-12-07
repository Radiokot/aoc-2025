fun main() {
    fun part1(input: InputStrings): Any {
        val field = input
            .filterNotEmpty()

        var lastRowBeamCellIndices = setOf<Int>()
        var splitCount = 0

        // Calculate beam propagation per each row,
        // considering calculation from the last (previous) row.
        // Need to know which cells in a row the beam goes through.
        // Count the splits.
        field.forEach { row ->
            val currentRowBeamCellIndices = mutableSetOf<Int>()

            row.forEachIndexed { cellIndex, cell ->
                if (cell == BEAM_SOURCE) {
                    // If the beam source in this cell,
                    // sure the beam goes through it.
                    currentRowBeamCellIndices += cellIndex
                } else if (cellIndex in lastRowBeamCellIndices) {
                    // Only proceed if the beam goes towards this cell.

                    if (cell == FREE) {
                        // If this cell is free, the beam goes through.
                        currentRowBeamCellIndices += cellIndex
                    } else if (cell == BEAM_SPLITTER) {
                        // If this cell is a splitter, beams emit from free cells around it.
                        if (row.getOrNull(cellIndex - 1) == FREE) {
                            currentRowBeamCellIndices += (cellIndex - 1)
                        }
                        if (row.getOrNull(cellIndex + 1) == FREE) {
                            currentRowBeamCellIndices += (cellIndex + 1)
                        }
                        splitCount++
                    }
                }
            }

            lastRowBeamCellIndices = currentRowBeamCellIndices
        }

        return splitCount
    }

    fun part2(input: InputStrings): Any {
        val field = input
            .filterNotEmpty()

        var lastRowBeamWaysByCellIndex = listOf<Long>()

        // Calculate beam propagation per each row,
        // considering calculation from the last (previous) row.
        // Need to know how many ways (timelines) there are for a beam to go through each position.
        // The result totals up in the last line, what's left is to sum the values.
        field.forEach rowLoop@{ row ->
            val currentRowBeamWaysByCellIndex = MutableList(row.length) { 0L }

            row.forEachIndexed cellLoop@{ cellIndex, cell ->
                if (cell == BEAM_SOURCE) {
                    // If the beam source in this cell,
                    // sure there's one way for the beam to go through it.
                    currentRowBeamWaysByCellIndex[cellIndex] = 1
                }

                // Only proceed if there are ways for the beam to go towards this cell.
                val waysTowardsThisCell = lastRowBeamWaysByCellIndex
                    .getOrNull(cellIndex)
                    ?.takeIf { it > 0 }
                    ?: return@cellLoop

                if (cell == FREE) {
                    // If this cell is free, the beam goes through
                    // the same number of ways it goes towards this cell.
                    currentRowBeamWaysByCellIndex[cellIndex] += waysTowardsThisCell
                } else if (cell == BEAM_SPLITTER) {
                    // If this cell is a splitter, beams emit from free cells around it
                    // the same number of ways the original beam goes towards the splitter.
                    // But if 2 splitters emit through the same free cell,
                    // the number of ways through this cell must be added up.
                    if (row.getOrNull(cellIndex - 1) == FREE) {
                        currentRowBeamWaysByCellIndex[cellIndex - 1] += waysTowardsThisCell
                    }
                    if (row.getOrNull(cellIndex + 1) == FREE) {
                        currentRowBeamWaysByCellIndex[cellIndex + 1] += waysTowardsThisCell
                    }
                }
            }

            lastRowBeamWaysByCellIndex = currentRowBeamWaysByCellIndex
        }

        return lastRowBeamWaysByCellIndex.sum()
    }

    val input =
//        readInput("Day07_test")
        readInput("Day07")

    part1(input).println()
    part2(input).println()
}

private const val BEAM_SOURCE = 'S'
private const val BEAM_SPLITTER = '^'
private const val FREE = '.'
