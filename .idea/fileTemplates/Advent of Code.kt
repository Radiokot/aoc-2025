#set( $Code = "bar" )
fun main() {
    fun part1(input: List<String>): Any {
        return input.size
    }

    fun part2(input: List<String>): Any {
        return input.size
    }

    val input =
        readInput("Day$Day")
//        readInput("Day${Day}_test")

    part1(input).println()
    part2(input).println()
}
