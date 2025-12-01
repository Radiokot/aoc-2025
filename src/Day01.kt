fun main() {
    fun part1(input: List<String>): Int {
        var dial = 50
        var password = 0
        input
            .filter(String::isNotEmpty)
            .map(::getDirectionAndClicks)
            .forEach { (direction, clicks) ->
                repeat(clicks) {
                    dial = (100 + dial + direction) % 100
                }
                if (dial == 0) {
                    password++
                }
            }

        return password
    }

    fun part2(input: List<String>): Int {
        var dial = 50
        var password = 0
        input
            .filter(String::isNotEmpty)
            .map(::getDirectionAndClicks)
            .forEach { (direction, clicks) ->
                repeat(clicks) {
                    dial = (100 + dial + direction) % 100
                    if (dial == 0) {
                        password++
                    }
                }
            }

        return password
    }

    val input =
        readInput("Day01")
//        readInput("Day01_test")

    part1(input).println()
    part2(input).println()
}

private fun getDirectionAndClicks(instruction: String): Pair<Int, Int> {
    val direction =
        if (instruction.take(1) == "L")
            -1
        else
            1
    val clicks = instruction.drop(1).toInt()

    return direction to clicks
}
