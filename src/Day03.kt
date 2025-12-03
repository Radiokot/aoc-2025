fun main() {
    fun part1(input: List<String>): Any {
        return input
            .filter(String::isNotEmpty)
            .sumOf { bank ->
                maxJoltageOf(
                    bank = bank,
                    batteriesToEnable = 2,
                )
            }
    }

    fun part2(input: List<String>): Any {
        return input
            .filter(String::isNotEmpty)
            .sumOf { bank ->
                maxJoltageOf(
                    bank = bank,
                    batteriesToEnable = 12,
                )
            }
    }

    val input =
        readInput("Day03")
//        readInput("Day03_test")

    part1(input).println()
    part2(input).println()
}

private fun maxJoltageOf(
    bank: String,
    batteriesToEnable: Int,
): Long {
    // The hint that helped me with this solution is that if 12 batteries must be enabled,
    // then the first one must be not closer to the end of the bank than 11,
    // and it must be the strongest one obviously.
    // Then repeated the process for now remaining 11 batteries to enable,
    // but now also skip the last enabled battery and all the batteries to its left.
    var maxJoltage = ""
    var lastEnabledBatteryIndex = -1
    (batteriesToEnable downTo 1).forEach { batteriesRemaining ->
        val remainingBank = bank.dropLast(batteriesRemaining - 1).drop(lastEnabledBatteryIndex + 1)
        val maxBattery = remainingBank.max()
        lastEnabledBatteryIndex += 1 + remainingBank.indexOf(maxBattery)
        maxJoltage += maxBattery
    }
    return maxJoltage.toLong()
}
