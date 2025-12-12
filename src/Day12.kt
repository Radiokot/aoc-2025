import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() {
    fun part1(input: InputStrings): Any = runBlocking(Dispatchers.Default) {
        val (presentShapes, regions) = input.toTask()

        regions
            .map { async { checkRegion(it, presentShapes) } }
            .awaitAll()
            .count { it }
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

private fun checkRegion(
    region: Region,
    presentShapes: List<PresentShape>
): Boolean {
    val allRequiredPresents =
        region
            .requiredPresentCount
            .zip(presentShapes)
            .flatMap { (count, shape) -> List(count) { shape } }

    if (allRequiredPresents.sumOf(PresentShape::area) > region.area) {
        return false
    }

    // Since this task is a prank,
    // returning true at this point is enough to get the right answer.

    // return true

    // Having all the flips and rotations pre-calculated saves time.
    val flipsAndRotationsByPresentShape: Map<PresentShape, List<List<PresentShape>>> =
        presentShapes.associateWith { shape ->
            listOf(shape, shape.flipped()).map { flippedShape ->
                (0..3).map { rotationCount ->
                    var rotatedShape = flippedShape
                    repeat(rotationCount) { rotatedShape = rotatedShape.rotatedClockwise() }
                    rotatedShape
                }
            }
        }

    fun canPlace(
        presents: List<PresentShape>,
        freeSpace: List<List<Boolean>>,
    ): Boolean {
        if (presents.isEmpty()) {
            return true
        }

        val presentToPlace = presents.last()
        val remainingPresents = presents.dropLast(1)

        return (0..region.height - presentToPlace.height).any { startRow ->
            (0..region.width - presentToPlace.width).any { startColumn ->
                (0..3).any { rotationCount ->
                    (0..1).any attempt@{ flipCount ->
                        val shapeToPlace =
                            flipsAndRotationsByPresentShape
                                .getValue(presentToPlace)[flipCount][rotationCount]

                        val freeSpaceAfterPlacement =
                            freeSpace
                                .map(List<Boolean>::toMutableList)

                        (0 until shapeToPlace.height).forEach { shapeRow ->
                            (0 until shapeToPlace.width).forEach { shapeColumn ->
                                if (shapeToPlace[shapeRow][shapeColumn]) {
                                    if (freeSpace[startRow + shapeRow][startColumn + shapeColumn]) {
                                        freeSpaceAfterPlacement[startRow + shapeRow][startColumn + shapeColumn] =
                                            false
                                    } else {
                                        // Trying to place over a taken cell,
                                        // this attempt is failed.
                                        return@attempt false
                                    }
                                }
                            }
                        }

                        return@attempt canPlace(
                            presents = remainingPresents,
                            freeSpace = freeSpaceAfterPlacement,
                        )
                    }
                }
            }
        }
    }

    return canPlace(
        presents = allRequiredPresents,
        freeSpace = List(region.height) { List(region.width) { true } },
    ).also {
        println("Region $region: $it")
    }
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
