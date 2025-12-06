fun main() {
    fun part1(input: InputStrings): Any {
        val spaceRegex = "\\s+".toRegex()
        val sheet = input.filterNotEmpty()

        val operands =
            sheet
                .dropLast(1)
                .map { row ->
                    row
                        .split(spaceRegex)
                        .filterNotEmpty()
                        .map(String::toLong)
                }
                // Turn rows into columns for convenience.
                .transpose()

        val operators: List<Char> =
            sheet
                .last()
                .split(spaceRegex)
                .filterNotEmpty()
                .map(String::first)

        return operators.zip(operands)
            .sumOf { (operator, operands) ->
                operands.compute(operator)
            }
    }

    fun part2(input: InputStrings): Any {
        val sheet = input.filterNotEmpty()
        // Rows in the sheet lack end space padding.
        val sheetWidth = sheet.maxOf(String::length)

        var sumOfOperations = 0L
        val readOperands = mutableListOf<Long>()

        (sheetWidth - 1 downTo 0).forEach { charIndex ->
            val verticallyReadValue =
                sheet
                    .mapNotNull { row ->
                        row
                            .getOrNull(charIndex)
                            ?.takeUnless(Char::isWhitespace)
                    }
                    .joinToString(separator = "")
                    .takeIf(String::isNotEmpty)
                    ?: return@forEach

            readOperands +=
                verticallyReadValue
                    .trim('*', '+')
                    .toLong()

            // Seems that the operator is always under the last operand.
            if (!verticallyReadValue.last().isDigit()) {
                val operator = verticallyReadValue.last()
                sumOfOperations += readOperands.compute(operator)
                readOperands.clear()
            }
        }

        return sumOfOperations
    }

    val input =
//        readInput("Day06_test")
        readInput("Day06")

    part1(input).println()
    part2(input).println()
}

private fun List<List<Long>>.transpose(): List<List<Long>> =
    this[0]
        .indices
        .map { cellIndex ->
            this.indices.map { rowIndex ->
                this[rowIndex][cellIndex]
            }
        }

private fun List<Long>.compute(operator: Char): Long =
    if (operator == '*')
        product()
    else
        sum()

private fun List<Long>.product(): Long =
    if (isEmpty())
        0L
    else
        fold(1L) { product, operand ->
            product * operand
        }
