#set( $Code = "bar" )
fun main() {
    fun part1(input: InputStrings): Any {
        return input.size
    }

    fun part2(input: InputStrings): Any {
        return input.size
    }

    val input =
        readInput("Day${Day}_test")
//        readInput("Day$Day")

    part1(input).println()
    part2(input).println()
}
