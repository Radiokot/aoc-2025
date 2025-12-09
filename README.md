# Advent of Code 2025 in Kotlin

Welcome to the [Advent of Code][aoc] Kotlin project created by [Radiokot][github] using
the [Advent of Code Kotlin Template][template] delivered by JetBrains.

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

Because of the problem being so easy, the JetBrains stream this day was all about Kotlin Notebook,
which I found impressive.

## [Day 5](https://adventofcode.com/2025/day/5)

For the first part I just programmed what was described in the problem:
parse ranges (`LongRange`), then count IDs present in at least one range.

For the second part I initially tried to put all the parsed range values into a set
by doing `flatMapTo(mutableSetOf(), LongRange::toSet)` on `List<LongRange>` –
it was not a big surprise when this didn't work because of the huge range sizes.
Then I decided to create a set of ranges that do not intersect and sum their sizes,
which worked.

How a new range can intersect with the already known ranges from the set, and what to do with it?

1. It can fully cover one or few known ranges – then get rid of these ranges
2. It can start inside a known range – then make it start right after this range
3. It can end inside a known range – then make it end right before this range

If both #2 and #3 are true and point to the same range, this means the new range
is fully inside a known one, in which case the new one is discarded.

Later I learned that this can be easier if the ranges are first sorted by the start,
but I left my solution intact.

## [Day 6](https://adventofcode.com/2025/day/6)

For the first part I transformed the input into an operand matrix and a list of operators.
Then I transposed the operand matrix for convenience – columns became rows
so each individual row now contained operands of the same problem.

For the second part I decided to implement what was described in the problem:
reading the sheet vertically from right to left, figuring out operands of the problem,
finding the operator, computing the problem and moving on.
This didn't work from the first try – I discovered the following caveats:

1. Rows in the input don't have space padding at the end, they are of different length
2. To parse the value read vertically as a number, I needed to do something with spaces in it.
   I initially wanted to remove them with `trim()`, but found out that in the column with the operator
   it is possible to read a value with spaces in between, like `_12__+`, which required special treatment

## [Day 7](https://adventofcode.com/2025/day/7)

Having the experience of the previous year, I know a problem with something doubling in size each iteration
(in this case, a row) is a problem of calculating each iteration considering the result from the previous iteration.
The answer then totals up in the last iteration calculation.

For the first part, I summed up number of spits while calculating through which cells a beam goes each row.

For the second part, I tracked not just a fact of a beam going through a cell, but also how many ways there are
for a beam to reach this cell and if the cell affects number of ways further. A last year caveat I forgot of
was using `Long` when counting rapidly doubling numbers.

## [Day 8](https://adventofcode.com/2025/day/8)

Was it a graph problem? I'm not sure, but I certainly don't want to remember graph algorithms once again,
so it's a relief I didn't have to do this today.

I started by obtaining a sorted list of distances between different points, without duplicates (`A<->B` = `B<->A`).
Did it by comparing all the points to each other.
As for the circuits, decided to store them as sets of points, starting with each point being its own circuit.
Point connection then is a function that finds circuits of both points, glues them together
and replaces the two with the glued one.

Part 1 does the required number of connections between the closest points, 
then returns the requested circuit size product.
Part 2 connects closest points until the glued circuit is big enough (contains all the points),
then returns the requested X coordinate product.

## [Day 9](https://adventofcode.com/2025/day/9)

Geometry problems were hard for me last year, this one was hard too.

For the first part, bruteforce solution runs very quick – 
just try all the possible rectangles and find the largest one.

For the second part, the best candidates must be filtered out.
I thought of all the possible corner cases and didn't manage to come up with an inside/outside check
that could satisfy them all. However, when I visualized the input data with `BufferedImage` and `Graphics2D`,
the shape turned out to be quite simple. My friend gave me a clue on how to check coordinates against the perimeter,
and the filter worked after running for about 3 seconds:

<img src="day-9-visualisation.png" width="500"  alt="Visualization"/>

1. I checked if any of the rectangle corners is obviously outside the perimeter,
   such rectangles can be filtered out right away;
2. If a rectangle has all their corners inside the perimeter, it still could cross some of the outside area,
   which is the case in the input data.
   I checked if any of the rectangle edges is perpendicularly crossed by the perimeter.

I don't like this solution. Although a star is acquired, I think if the shape wasn't a simple circle
with one straight cut in the middle, my checks wouldn't work or would run for too long.

Roman Elizarov just knew the algorithm for this problem, obviously.
On Reddit, I've seen people getting the right answer
with quite a long-running bruteforces, much longer than 3 seconds.
One guy tried randomly picking some number of points within a rectangle
and checking them for being a perimeter, then assuming the whole rectangle is correct – it worked too.

[aoc]: https://adventofcode.com

[github]: https://github.com/radiokot

[issues]: https://github.com/kotlin-hands-on/advent-of-code-kotlin-template/issues

[kotlin]: https://kotlinlang.org

[slack]: https://surveys.jetbrains.com/s3/kotlin-slack-sign-up

[template]: https://github.com/kotlin-hands-on/advent-of-code-kotlin-template
