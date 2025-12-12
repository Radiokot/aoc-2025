fun main() {
    fun part1(input: InputStrings): Any {
        val (presentShapes, regions) = input.toTask()

        return regions.count { region ->
            val areaOfAllRequiredPresents =
                region
                    .requiredPresentCount
                    .zip(presentShapes)
                    .sumOf { (count, shape) -> shape.area * count }

            // LOL.
            return@count region.area >= areaOfAllRequiredPresents
        }
    }

    fun part2(input: InputStrings): Any {
        return input.size
    }

    val input =
//        readInput("Day12_test")
        readInput("Day12")

    part1(input).println()
    part2(input).println()
}

private typealias PresentShape = List<List<Boolean>>

private val PresentShape.width: Int
    get() = this[0].size

private val PresentShape.height: Int
    get() = this.size

private fun PresentShape.flipped(): PresentShape =
    map(List<Boolean>::reversed)

private fun PresentShape.rotatedClockwise(): PresentShape {
    return indices.map { newRowIndex ->
        this[0].indices.map { newColumnIndex ->
            this[height - 1 - newColumnIndex][newRowIndex]
        }
    }
}

private fun PresentShape.toShapeString(): String =
    joinToString(
        separator = "\n",
        transform = { row ->
            row.joinToString(
                separator = "",
                transform = { if (it) "#" else "." },
            )
        },
    )

private val PresentShape.area: Int
    get() =
        sumOf { line ->
            line.count { it }
        }

private data class Task(
    val presentShapes: List<PresentShape>,
    val regions: List<Region>,
)

private class Region(
    val width: Int,
    val height: Int,
    val requiredPresentCount: List<Int>,
) {
    val area: Int =
        width * height
}

private fun InputStrings.toTask(): Task {
    val presentShapes = mutableListOf<PresentShape>()
    val regions = mutableListOf<Region>()
    val lineIterator = iterator()

    while (lineIterator.hasNext()) {
        val line = lineIterator.next()

        if (line.isEmpty()) {
            continue
        }

        if (presentShapes.size < 6) {
            // Read present shape.
            presentShapes.add(
                (1..3).map {
                    val shapeLine = lineIterator.next()
                    shapeLine.map { it == '#' }
                }
            )
        } else {
            // Read region.
            val (width, height) =
                line
                    .substringBefore(':')
                    .split('x')
                    .map(String::toInt)

            val requiredPresentCount =
                line
                    .substringAfter(": ")
                    .split(' ')
                    .map(String::toInt)

            regions.add(
                Region(width, height, requiredPresentCount)
            )
        }
    }

    return Task(presentShapes, regions)
}
