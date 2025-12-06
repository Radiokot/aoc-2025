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
        val columnCount = sheet.maxOf(String::length)

        var sumOfOperations = 0L
        val readOperands = mutableListOf<Long>()

        (columnCount - 1 downTo 0).forEach { columnIndex ->
            val columnValue =
                sheet
                    .mapNotNull { row ->
                        row.getOrNull(columnIndex)
                    }
                    .joinToString(separator = "")
                    .takeIf(String::isNotBlank)
                    ?: return@forEach

            // If column value is not blank, there's always an operand in it,
            // read it discarding spaces and an operator.
            readOperands +=
                columnValue
                    .filter(Char::isDigit)
                    .toLong()

            // There may be an operator in this column,
            // which is not a digit and not a space.
            val operator: Char? =
                columnValue
                    .find { !it.isDigit() && !it.isWhitespace() }

            // If the operator is in this column,
            // it's the end of this problem.
            if (operator != null) {
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
