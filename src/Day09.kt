import java.awt.BasicStroke
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: InputStrings): Any {
        val pointSequence = input.toPointSequence()
        var maxArea = 0L
        pointSequence.forEach { pointA ->
            pointSequence.forEach { pointB ->
                if (pointA != pointB) {
                    maxArea = max(maxArea, pointA.rectangleArea(pointB))
                }
            }
        }
        return maxArea
    }

    fun part2(input: InputStrings): Any {
        val visualisationImage = BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB)
        val pointSequence = input.toPointSequence()

        // Actually store all the perimeter points.
        val allRedPoints = pointSequence.toList()
        val perimeterPointCoordinates = mutableMapOf<Int, MutableSet<Int>>()
        allRedPoints.forEachIndexed { i, redPoint ->
            val previousRedPoint =
                allRedPoints
                    .getOrNull(i - 1)
                    ?: allRedPoints.last()
            (min(previousRedPoint.x, redPoint.x)..max(previousRedPoint.x, redPoint.x)).forEach { x ->
                (min(previousRedPoint.y, redPoint.y)..max(previousRedPoint.y, redPoint.y)).forEach { y ->
                    perimeterPointCoordinates
                        .getOrPut(y, ::mutableSetOf)
                        .add(x)

                    visualisationImage.setRGB(
                        x / 100,
                        y / 100,
                        Color(255, 255, 255).rgb,
                    )
                }
            }
        }

        val sortedRectangleSequence: Sequence<Rectangle> =
            pointSequence
                .flatMap { pointA ->
                    pointSequence.mapNotNull { pointB ->
                        if (pointA != pointB)
                            Rectangle(pointA, pointB)
                        else
                            null
                    }
                }
                .sortedByDescending(Rectangle::area)

        // Check candidates starting from the largest.
        sortedRectangleSequence.forEachIndexed candidateLoop@{ i, candidate ->
            if (i % 10000 == 0) {
                println("Checked $i candidates")
            }

            val topLeftX = min(candidate.first.x, candidate.second.x)
            val topLeftY = min(candidate.first.y, candidate.second.y)
            val bottomRightX = max(candidate.first.x, candidate.second.x)
            val bottomRightY = max(candidate.first.y, candidate.second.y)

            // First, check if any of the corners is obviously outside the perimeter.
            // Ignore candidates with a corner outside.
            val topLeftCorner = Point2(topLeftX, topLeftY)
            val isTopLeftCornerOutside =
                perimeterPointCoordinates[topLeftCorner.y]!!
                    .none { it <= topLeftCorner.x }
            val topRightCorner = Point2(bottomRightX, topLeftY)
            val isTopRightCornerOutside =
                perimeterPointCoordinates[topRightCorner.y]!!
                    .none { it >= topRightCorner.x }
            val bottomLeftCorner = Point2(topLeftX, bottomRightY)
            val isBottomLeftCornerOutside =
                perimeterPointCoordinates[bottomLeftCorner.y]!!
                    .none { it <= bottomLeftCorner.x }
            val bottomRightCorner = Point2(bottomRightX, bottomRightY)
            val isBottomRightCornerOutside =
                perimeterPointCoordinates[bottomRightCorner.y]!!
                    .none { it >= bottomRightCorner.x }

            if (isTopRightCornerOutside || isTopLeftCornerOutside
                || isBottomLeftCornerOutside || isBottomRightCornerOutside
            ) {
                return@candidateLoop
            }

            // Then check if outsideness crosses one or more edges of this precious rectangle:
            // ########
            // ########
            //    #####
            //    #####
            // ########
            // ########
            // For this, check if any edges are perpendicularly crossed by the perimeter.

            (topLeftX + 1 until bottomRightX).forEach { x ->
                val isTopEdgeCrossedByPerimeter =
                    perimeterPointCoordinates.containsPoint(x, topLeftY) &&
                            perimeterPointCoordinates.containsPoint(x, topLeftY - 1) &&
                            perimeterPointCoordinates.containsPoint(x, topLeftY + 1)
                val isBottomEdgeCrossedByPerimeter =
                    perimeterPointCoordinates.containsPoint(x, bottomRightY) &&
                            perimeterPointCoordinates.containsPoint(x, bottomRightY - 1) &&
                            perimeterPointCoordinates.containsPoint(x, bottomRightY + 1)

                if (isTopEdgeCrossedByPerimeter || isBottomEdgeCrossedByPerimeter) {
                    return@candidateLoop
                }
            }

            (topLeftY + 1 until bottomRightY).forEach { y ->
                val isLeftEdgeCrossedByPerimeter =
                    perimeterPointCoordinates.containsPoint(topLeftX, y) &&
                            perimeterPointCoordinates.containsPoint(topLeftX - 1, y) &&
                            perimeterPointCoordinates.containsPoint(topLeftX + 1, y)
                val isRightEdgeCrossedByPerimeter =
                    perimeterPointCoordinates.containsPoint(bottomRightX, y) &&
                            perimeterPointCoordinates.containsPoint(bottomRightX - 1, y) &&
                            perimeterPointCoordinates.containsPoint(bottomRightX + 1, y)
                if (isLeftEdgeCrossedByPerimeter || isRightEdgeCrossedByPerimeter) {
                    return@candidateLoop
                }
            }

            // At least for a simple shape from this problem input, this is enough.

            visualisationImage.draw {
                color = Color(255, 200, 100, 100)
                stroke = BasicStroke(2f)
                fillRect(
                    topLeftX / 100, topLeftY / 100,
                    (bottomRightX - topLeftX) / 100, (bottomRightY - topLeftY) / 100
                )
            }

            saveVisualisation(visualisationImage)

            return candidate.area()
        }

        error("It failed")
    }

    val input =
//        readInput("Day09_test")
        readInput("Day09")

    part1(input).println()
    part2(input).println()
}

private data class Point2(
    val x: Int,
    val y: Int
) {
    fun rectangleArea(oppositeCorner: Point2): Long {
        // A rectangle of 1 point has area of 1, not 0.
        val width = 1L + abs(oppositeCorner.x - this.x)
        val height = 1L + abs(oppositeCorner.y - this.y)
        return width * height
    }
}

private typealias Rectangle = Pair<Point2, Point2>

private fun Rectangle.area(): Long =
    first.rectangleArea(second)

private fun InputStrings.toPointSequence(): Sequence<Point2> =
    this
        .filterNotEmpty()
        .asSequence()
        .map { line ->
            val (xStr, yStr) = line.split(',')
            Point2(xStr.toInt(), yStr.toInt())
        }

private fun Map<Int, Set<Int>>.containsPoint(x: Int, y: Int): Boolean =
    this[y]?.contains(x) == true
