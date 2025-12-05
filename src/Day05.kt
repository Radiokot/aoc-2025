fun main() {
    fun part1(input: InputStrings): Any {
        val freshRanges = input.getFreshRanges()

        return input.count { string ->
            val id = string.toLongOrNull()
                ?: return@count false
            freshRanges.any { id in it }
        }
    }

    fun part2(input: InputStrings): Any {
        val nonIntersectingRanges = mutableSetOf<LongRange>()

        input.getFreshRanges().forEach { candidateRange ->
            nonIntersectingRanges.removeIf { it.first in candidateRange && it.last in candidateRange }

            val rangeContainingStart: LongRange? =
                nonIntersectingRanges.find { candidateRange.first in it }

            val rangeContainingEnd: LongRange? =
                nonIntersectingRanges.find { candidateRange.last in it }

            if (rangeContainingStart != null && rangeContainingStart == rangeContainingEnd) {
                return@forEach
            }

            val adjustedStart =
                if (rangeContainingStart != null)
                    rangeContainingStart.last + 1
                else
                    candidateRange.first

            val adjustedEnd =
                if (rangeContainingEnd != null)
                    rangeContainingEnd.first - 1
                else
                    candidateRange.last

            nonIntersectingRanges += adjustedStart..adjustedEnd
        }

        return nonIntersectingRanges.sumOf { range ->
            (range.last - range.first) + 1
        }
    }

    val input =
//        readInput("Day05_test")
        readInput("Day05")

    part1(input).println()
    part2(input).println()
}

private fun InputStrings.getFreshRanges(): List<LongRange> =
    mapNotNull { string ->
        val split = string.split('-')
        if (split.size == 1) {
            return@mapNotNull null
        }
        LongRange(split[0].toLong(), split[1].toLong())
    }
