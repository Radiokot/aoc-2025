fun main() {
    fun part1(input: InputStrings): Any {
        val grid = input.toGrid()
        var accessibleCellCount = 0

        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell && grid.isCellAccessible(x, y)) {
                    accessibleCellCount++
                }
            }
        }

        return accessibleCellCount
    }

    fun part2(input: InputStrings): Any {
        val grid = input.toGrid()
        var removedCellCount = 0

        do {
            var anyRemovals = false
            grid.forEachIndexed { y, row ->
                row.forEachIndexed { x, cell ->
                    if (cell && grid.isCellAccessible(x, y)) {
                        grid[y][x] = false
                        anyRemovals = true
                        removedCellCount++
                    }
                }
            }
        } while (anyRemovals)

        return removedCellCount
    }

    val input =
        readInput("Day04")
//        readInput("Day04_test")

    part1(input).println()
    part2(input).println()
}

private typealias Grid = List<MutableList<Boolean>>

private fun InputStrings.toGrid(): Grid =
    this
        .filterNotEmpty()
        .map { row ->
            row.mapTo(mutableListOf()) {
                it == '@'
            }
        }

private fun Grid.isCellAccessible(x: Int, y: Int): Boolean {
    var occupiedCellsAround = 0

    if (get(y).getOrNull(x - 1) == true)
        occupiedCellsAround++
    if (get(y).getOrNull(x + 1) == true)
        occupiedCellsAround++
    if (getOrNull(y - 1)?.get(x) == true)
        occupiedCellsAround++
    if (getOrNull(y + 1)?.get(x) == true)
        occupiedCellsAround++
    if (getOrNull(y - 1)?.getOrNull(x - 1) == true)
        occupiedCellsAround++
    if (getOrNull(y - 1)?.getOrNull(x + 1) == true)
        occupiedCellsAround++
    if (getOrNull(y + 1)?.getOrNull(x - 1) == true)
        occupiedCellsAround++
    if (getOrNull(y + 1)?.getOrNull(x + 1) == true)
        occupiedCellsAround++

    return occupiedCellsAround < 4
}
