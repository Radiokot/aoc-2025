# Advent of Code 2025 in Kotlin

Welcome to the [Advent of Code][aoc] Kotlin project created by [Radiokot][github] using the [Advent of Code Kotlin Template][template] delivered by JetBrains.

## [Day 1](https://adventofcode.com/2025/day/1)

I decided to simply simulate rotation in a loop.
To keep the dial positive, I used `dial = (100 + dial + direction) % 100`.
Later I learned from the JetBrains stream that the same can be achieved with the `mod` function.
The stream also had an Amper demo, and an impressive Compose visualisation of the rotation.

## [Day 2](https://adventofcode.com/2025/day/2)

For the first part I implemented a string symmetry check.

For the second part I tried splitting a string in same length chunks and then checking if they are the same,
which means the string only consists of repeats of the chunk.
In Kotlin, it's really easy: `if (idString.chunked(length).distinct().size == 1)`
I stumbled upon getting too big count because initially I forgot to break the testing loop,
which counted IDs like `222222` multiple times (`222222` is 2x6 but also 22x3 and 222x2).

## [Day 3](https://adventofcode.com/2025/day/3)

For the first part I wrote loop that tests every possible first digit combining it
with the max digit from what's left to the right. If only had I realized that the testing is pointless,
and you just need to pick the max one too, the second part would have been easier for me.

I spent 1.5 hours implementing a recursive bruteforce for the second part
and then hopelessly trying to make it run on the big input lines.
I thought it needed a dynamic programming solution, but since I don't understand it,
I gave up and went to Reddit for a hint.

The hint that helped me with the solution is that if 12 batteries must be enabled,
then the first one must be not closer to the right end of the bank than 11,
and it must be the strongest one obviously.
Then the process is repeated for now remaining 11 batteries to enable,
but now the last enabled battery and all the batteries to its left must be skipped.

## [Day 4](https://adventofcode.com/2025/day/4)

The solution was straightforward, I just programmed what was described in the problem and it worked.
I spent more time on prettifying the file template and utility functions for this solution.
I represented the grid as `List<MutableList<Boolean>>`, and in the cell accessibility check function
I called `getOrNull()` getters to avoid manual checks for going out of range.

I liked how Roman Elizarov used two inline `(-1..1)` loops to count occupied space around a cell,
and then used `<=4` condition instead of `<4` to address counting the cell itself as occupied.
Usually I don't understand his code at all.

[aoc]: https://adventofcode.com
[github]: https://github.com/radiokot
[issues]: https://github.com/kotlin-hands-on/advent-of-code-kotlin-template/issues
[kotlin]: https://kotlinlang.org
[slack]: https://surveys.jetbrains.com/s3/kotlin-slack-sign-up
[template]: https://github.com/kotlin-hands-on/advent-of-code-kotlin-template
