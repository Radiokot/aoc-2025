import kotlin.math.sqrt

fun main() {
    fun part1(
        input: InputStrings,
        connectionCount: Int,
    ): Any {
        val pointSequence: Sequence<Point3> = input.toPointSequence()

        // Distances from smallest to largest.
        val sortedDistances: List<Distance> =
            pointSequence.getSortedDistances()

        // Start with each point being its own circuit (set of points).
        val circuits: MutableSet<Circuit> =
            pointSequence.mapTo(mutableSetOf()) { point ->
                setOf(point)
            }

        // Do the required number of connections.
        sortedDistances.take(connectionCount).forEach { nextMinDistance ->
            val (_, pointA, pointB) = nextMinDistance
            circuits.connect(pointA, pointB)
        }

        return circuits
            .sortedByDescending(Set<*>::size)
            .take(3)
            .fold(1) { product, circuit ->
                product * circuit.size
            }
    }

    fun part2(input: InputStrings): Any {
        val pointSequence: Sequence<Point3> = input.toPointSequence()
        val pointCount = pointSequence.count()

        // Distances from smallest to largest.
        val sortedDistances: List<Distance> =
            pointSequence.getSortedDistances()

        // Start with each point being its own circuit (set of points).
        val circuits: MutableSet<Circuit> =
            pointSequence.mapTo(mutableSetOf()) { point ->
                setOf(point)
            }

        // Connect until once circuit has all the points.
        sortedDistances.forEach { nextMinDistance ->
            val (_, pointA, pointB) = nextMinDistance
            val connectedCircuit = circuits.connect(pointA, pointB)
            if (connectedCircuit.size == pointCount) {
                return pointA.x * pointB.x
            }
        }

        error("It failed to connect all the points")
    }

    val isTest = false
    if (isTest) {
        val input = readInput("Day08_test")
        part1(input, connectionCount = 10).println()
        part2(input).println()
    } else {
        val input = readInput("Day08")
        part1(input, connectionCount = 1000).println()
        part2(input).println()
    }
}

private data class Point3(
    val x: Long,
    val y: Long,
    val z: Long
) {
    fun distanceTo(other: Point3): Distance {
        val distance = sqrt(
            ((x - other.x) * (x - other.x) +
                    (y - other.y) * (y - other.y) +
                    (z - other.z) * (z - other.z)).toDouble()
        )
        return Distance(distance, this, other)
    }
}

private typealias Distance = Triple<Double, Point3, Point3>

private typealias Circuit = Set<Point3>

private fun InputStrings.toPointSequence(): Sequence<Point3> =
    this
        .filterNotEmpty()
        .asSequence()
        .map { line ->
            val (x, y, z) = line.split(',')
            Point3(x.toLong(), y.toLong(), z.toLong())
        }

private fun Sequence<Point3>.getSortedDistances(): List<Triple<Double, Point3, Point3>> =
    this
        .flatMapIndexed { pointAIndex, pointA ->
            this
                // Do not compare this point with points before it,
                // as it was already done in previous iterations.
                // +1 here prevents comparing a point to itself.
                .drop(pointAIndex + 1)
                .map { pointB ->
                    pointA.distanceTo(pointB)
                }
        }
        .toList()
        .sortedBy(Distance::first)

private fun MutableSet<Circuit>.connect(
    pointA: Point3,
    pointB: Point3
): Circuit {
    val pointACircuit = first { pointA in it }
    val pointBCircuit = first { pointB in it }
    val connectedCircuit = pointACircuit + pointBCircuit
    remove(pointACircuit)
    remove(pointBCircuit)
    add(connectedCircuit)
    return connectedCircuit
}
