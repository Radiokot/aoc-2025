import io.ksmt.KContext
import io.ksmt.solver.KSolverStatus
import io.ksmt.solver.z3.KZ3Solver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds
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

    fun part2(input: InputStrings): Any {
        val machines = input.toMachines()
        val mathContext = KContext()

        return machines.sumOf { machine ->
            KZ3Solver(mathContext).use { solver ->
                with(mathContext) {
                    // Searching for press count for each button
                    // by solving each machine as an equation system.

                    val searchedButtonPressCounts = machine.buttons.indices.map { buttonIndex ->
                        mkConst("button${buttonIndex}PressCount", mkIntSort())
                    }

                    // For each joltage, add an equation:
                    // sum of all the presses of buttons affecting this joltage = joltage
                    // (each button press increments joltage by 1).
                    machine.requiredJoltages.forEachIndexed { joltageIndex, joltage ->
                        solver.assert(
                            mkEq(
                                mkArithAdd(
                                    machine
                                        .buttons
                                        .mapIndexed { buttonIndex, button ->
                                            mkArithMul(
                                                searchedButtonPressCounts[buttonIndex],
                                                mkIntNum(if (joltageIndex in button.indices) 1 else 0)
                                            )
                                        }
                                ),
                                mkIntNum(joltage)
                            )
                        )
                    }

                    // Also add inequalities setting bounds
                    // for each searched button press count:
                    // count >= 0 and count <= min joltage this button affects
                    machine.buttons.forEachIndexed { buttonIndex, button ->
                        solver.assert(
                            mkArithGe(
                                searchedButtonPressCounts[buttonIndex],
                                mkIntNum(0)
                            )
                        )
                        solver.assert(
                            mkArithLe(
                                searchedButtonPressCounts[buttonIndex],
                                mkIntNum(
                                    machine
                                        .requiredJoltages
                                        .withIndex()
                                        .filter { it.index in button.indices }
                                        .minOf { it.value }
                                )
                            )
                        )
                    }

                    val pressCountSumExpression = mkArithAdd(searchedButtonPressCounts)
                    var minPressCountSum: Int? = null

                    // There can be many solutions.
                    // Solver gives one, but it is not the best one.
                    // Add more and more "sum of press counts < previous sum" inequalities
                    // until the system is not solvable,
                    // the last sum then is the minimal one.
                    while (solver.check(10.seconds) == KSolverStatus.SAT) {
                        minPressCountSum =
                            solver
                                .model()
                                .eval(pressCountSumExpression)
                                .toString()
                                .toInt()
                        solver.assert(
                            mkArithLt(
                                pressCountSumExpression,
                                mkIntNum(minPressCountSum)
                            )
                        )
                    }

                    checkNotNull(minPressCountSum) {
                        "Machine $machine failed to solve"
                    }

                    minPressCountSum.toLong()
                }
            }
        }
    }

    val input =
//        readInput("Day10_test")
        readInput("Day10")

//    part1(input).println()
    part2(input).println()
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
