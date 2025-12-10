import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.math.min
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalAtomicApi::class)
fun main() {
    fun part1(input: InputStrings): Any = runBlocking(Dispatchers.Default) {
        input
            .toMachines()
            .map { machine ->
                async {
                    var minButtonPressCount = Int.MAX_VALUE

                    repeat(100000) {
                        val lights = machine.requiredLights.mapTo(mutableListOf()) { false }
                        var buttonPressCount = 0
                        while (lights != machine.requiredLights) {
                            machine.buttons.random().toggleLights(lights)
                            buttonPressCount++
                        }
                        minButtonPressCount = min(minButtonPressCount, buttonPressCount)
                    }

                    minButtonPressCount
                }
            }
            .awaitAll()
            .sum()
    }

    fun part2(input: InputStrings): Any = runBlocking(Dispatchers.Default) {
        val machines = input.toMachines()
        val solvedCount = AtomicInt(0)

        return@runBlocking input
            .toMachines()
            .sortedBy { it.requiredJoltages.size }
            .mapIndexed { index, machine ->
                async {
                    val startTime = Clock.System.now()

                    val memoization = mutableMapOf<Any, Long?>()

                    suspend fun minPressesToReachRequiredJoltages(
                        currentJoltages: List<Int> = machine.requiredJoltages.map { 0 },
                    ): Long? {
                        yield()

                        val memoizationKey = currentJoltages

                        if (memoization.containsKey(memoizationKey)) {
                            return memoization[memoizationKey]
                        }

                        val unreachedJoltageIndices =
                            currentJoltages
                                .indices
                                .filterTo(mutableSetOf()) { index ->
                                    currentJoltages[index] < machine.requiredJoltages[index]
                                }

                        if (unreachedJoltageIndices.isEmpty()) {
                            memoization[memoizationKey] = 0
                            return 0
                        }

                        val possibleButtonsToPress =
                            machine
                                .buttons
                                .filter { unreachedJoltageIndices.containsAll(it.indices) }

                        val currentJoltages = currentJoltages.toMutableList()
                        var initialPresses = 0

                        val joltageIndicesWithSingleButton =
                            machine
                                .requiredJoltages
                                .indices
                                .map { joltageIndex ->
                                    joltageIndex to possibleButtonsToPress.filter { joltageIndex in it.indices }
                                }
                                .mapNotNull { (joltageIndex, buttons) ->
                                    if (buttons.size == 1)
                                        joltageIndex to buttons.first()
                                    else
                                        null
                                }

                        joltageIndicesWithSingleButton.forEach { (joltageIndex, button) ->
                            while (currentJoltages[joltageIndex] < machine.requiredJoltages[joltageIndex]) {
                                button.toggleJoltages(currentJoltages)
                                initialPresses++
                            }
                        }

                        if (possibleButtonsToPress.isEmpty()) {
                            memoization[memoizationKey] = null
                            return null
                        } else {
                            val anyUnreachableJoltages = unreachedJoltageIndices.any { index ->
                                currentJoltages[index] > machine.requiredJoltages[index]
                                        || possibleButtonsToPress.none { button ->
                                    index in button.indices
                                }
                            }
                            if (anyUnreachableJoltages) {
                                memoization[memoizationKey] = null
                                return null
                            }
                        }

                        return possibleButtonsToPress
                            .mapNotNull { button ->
                                minPressesToReachRequiredJoltages(
                                    currentJoltages = button.getToggledJoltages(currentJoltages)
                                )
                            }
                            .minOfOrNull { minPressesAfterPress ->
                                minPressesAfterPress + initialPresses + 1
                            }
                            .also { result ->
                                memoization[memoizationKey] = result
                            }
                    }

                    val endTime = Clock.System.now()

                    val result = minPressesToReachRequiredJoltages()
                        ?: error("Min presses not found for machine #$index")

                    val progressPercent = (solvedCount.incrementAndFetch().toDouble() / machines.size.toDouble()) * 100

                    println("$endTime: #$machine:\n$result, took ${endTime - startTime},\nprogress:$progressPercent%")

                    return@async result
                }
            }
            .awaitAll()
            .sum()
    }

    val input =
//        readInput("Day10_test")
        readInput("Day10")

    part1(input).println()
//    part2(input).println()
}

private fun List<Boolean>.toLightsString() =
    joinToString(
        separator = "",
        prefix = "[",
        postfix = "]",
        transform = { isLightOn ->
            if (isLightOn)
                "#"
            else
                "."
        }
    )

private class Machine(
    val requiredLights: List<Boolean>,
    val buttons: List<ToggleButton>,
    val requiredJoltages: List<Int>,
) {

    override fun toString(): String {
        return buildString {
            append(requiredLights.toLightsString())
            append(' ')
            append(
                buttons.joinToString(
                    separator = " ",
                    transform = { button ->
                        button.indices.joinToString(
                            separator = ",",
                            prefix = "(",
                            postfix = ")",
                        )
                    }
                )
            )
            append(' ')
            append(
                requiredJoltages.joinToString(
                    separator = ",",
                    prefix = "{",
                    postfix = "}"
                )
            )
        }
    }
}

private class ToggleButton(
    val indices: Set<Int>,
) {
    fun toggleLights(currentLights: MutableList<Boolean>) {
        indices.forEach { index ->
            currentLights[index] = !currentLights[index]
        }
    }

    fun toggleJoltages(currentJoltages: MutableList<Int>) {
        indices.forEach { index ->
            currentJoltages[index] += 1
        }
    }

    fun getToggledJoltages(currentJoltages: List<Int>): List<Int> =
        currentJoltages
            .toMutableList()
            .also(::toggleJoltages)
}

private fun InputStrings.toMachines(): List<Machine> =
    this
        .filterNotEmpty()
        .map { line ->
            val requiredLights =
                line
                    .substringAfter('[')
                    .substringBefore(']')
                    .map { it == '#' }

            val buttons =
                line
                    .substringAfter(']')
                    .substringBefore('{')
                    .trim()
                    .split(' ')
                    .map { buttonString ->
                        ToggleButton(
                            indices =
                                buttonString
                                    .trim('(', ')')
                                    .split(',')
                                    .mapTo(mutableSetOf(), String::toInt)
                        )
                    }

            val requiredJoltages =
                line
                    .substringAfter('{')
                    .substringBefore('}')
                    .split(',')
                    .map(String::toInt)

            Machine(
                requiredLights = requiredLights,
                buttons = buttons,
                requiredJoltages = requiredJoltages,
            )
        }
