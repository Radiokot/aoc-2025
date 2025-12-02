fun main() {
    fun part1(input: List<String>): Any {
        var invalidIdSum = 0L
        getRanges(input).forEach { range ->
            range.forEach { id ->
                val idString = id.toString()
                if (idString.length % 2 == 0
                    && idString.take(idString.length / 2) == idString.drop(idString.length / 2)
                ) {
                    invalidIdSum += id
                }
            }
        }

        return invalidIdSum
    }

    fun part2(input: List<String>): Any {
        var invalidIdSum = 0L
        getRanges(input).forEach { range ->
            range.forEach idLoop@{ id ->
                val idString = id.toString()
                (1..idString.length / 2).forEach { chunkLength ->
                    val chunkSet =
                        idString
                            .chunked(chunkLength)
                            .toSet()
                    if (chunkSet.size == 1) {
                        invalidIdSum += id
                        return@idLoop
                    }
                }
            }
        }

        return invalidIdSum
    }

    val input =
        readInput("Day02")
//        readInput("Day02_test")

    part1(input).println()
    part2(input).println()
}

private fun getRanges(input: List<String>): List<LongRange> {
    return input
        .first()
        .split(',')
        .map { rangeString ->
            rangeString
                .split('-')
                .let { it[0].toLong()..it[1].toLong() }
        }
}
