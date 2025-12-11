import java.util.*

fun main() {
    fun part1(input: InputStrings): Any {
        val connections = input.toDeviceConnections()

        // Breadth-first search modified to track paths
        // instead of individual visited nodes.

        val targetDevice = "out"
        val pathsToCheck = LinkedList<List<String>>()
        pathsToCheck.push(listOf("you"))

        var pathCount = 0

        while (pathsToCheck.isNotEmpty()) {
            val currentPath = pathsToCheck.pop()
            val lastDevice = currentPath.last()

            if (lastDevice == targetDevice) {
                pathCount++
                continue
            }

            connections[lastDevice]!!
                // Instead of checking if a node (device) was ever visited,
                // only check if it was visited in the current path.
                // This allows nodes to be re-visited when checking other paths.
                .filter { it !in currentPath }
                .map { connectedDevice ->
                    // Current path extended with the next node (device).
                    currentPath + connectedDevice
                }
                .also(pathsToCheck::addAll)
        }

        return pathCount
    }

    fun part2(input: InputStrings): Any {
        val connections = input.toDeviceConnections()

        val targetDevice = "out"
        val devicesToEncounter = setOf("dac", "fft")

        val memoization = mutableMapOf<Any, Long>()
        fun countProblematicPathsToTarget(
            fromDevice: String,
            encounteredDevices: Set<String> = emptySet(),
        ): Long {
            val memoizationKey = "$fromDevice${encounteredDevices.hashCode()}"
            if (memoization.containsKey(memoizationKey)) {
                return memoization[memoizationKey]!!
            }

            if (fromDevice == targetDevice) {
                // If the given node already is the target, then consider one problematic path found
                // if the specific 2 nodes have been encountered.
                val result = if (encounteredDevices == devicesToEncounter) 1L else 0L
                memoization[memoizationKey] = result
                return result
            }

            val encounteredDevicesAtThisPoint =
                if (fromDevice in devicesToEncounter)
                    encounteredDevices + fromDevice
                else
                    encounteredDevices

            // The number of problematic paths (containing 2 specific nodes)
            // from a given node is a sum of all the problematic paths from its children.
            return connections
                .getOrDefault(fromDevice, emptySet())
                .sumOf { connectedDevice ->
                    countProblematicPathsToTarget(
                        fromDevice = connectedDevice,
                        encounteredDevices = encounteredDevicesAtThisPoint,
                    )
                }
                .also { result ->
                    memoization[memoizationKey] = result
                }
        }

        return countProblematicPathsToTarget(
            fromDevice = "svr",
        ).also {
            println("Number of memoized results for part 2: ${memoization.size}")
        }
    }

    val isTest = false
    if (isTest) {
        part1(readInput("Day11_test_part1")).println()
        part2(readInput("Day11_test_part2")).println()
    } else {
        val input = readInput("Day11")
        part1(input).println()
        part2(input).println()
    }
}

private fun InputStrings.toDeviceConnections(): Map<String, Set<String>> =
    this
        .filterNotEmpty()
        .associate { line ->
            val adven = line.substringBefore(':')
            val fcode =
                line
                    .substringAfter(": ")
                    .split(' ')
                    .toSet()

            adven to fcode
        }
