package days

import utils.InputReader.readInput
import kotlin.math.min

object Day13 {

    private const val BLOCKS_PATTERN = "\n\n"
    fun puzzle1() {
        readInput("day13Input") { lines ->
            val blocks = lines.joinToString("\n").split(Regex(BLOCKS_PATTERN)).map { it.lines() }

            val result = blocks.sumOf { block -> block.findReflections(smudge = false) }
            println("Day 13 puzzle 1 result is: $result")
        }
    }

    fun puzzle2() {
        readInput("day13Input") { lines ->
            val blocks = lines.joinToString("\n").split(Regex(BLOCKS_PATTERN)).map { it.lines() }

            val result = blocks.sumOf { block -> block.findReflections(true) }
            println("Day 13 puzzle 2 result is: $result")
        }
    }

    private fun List<String>.findReflections(smudge: Boolean): Int {
        val vertical = findVerticalReflection(smudge)
        if (vertical != 0) return vertical

        val horizontal = findHorizontalReflection(smudge)
        if (horizontal != 0) return horizontal

        return 0
    }

    private fun List<String>.findVerticalReflection(smudge: Boolean): Int {
        val columns = first().mapIndexed { index, _ ->
            map { line -> line[index] }.joinToString("")
        }

        val verticalIndex = (columns.size - 1 downTo 1).firstOrNull { index ->
            isReflectionLine(columns[index], columns[index - 1], smudge) && columns.isValid(index, smudge)
        }

        return verticalIndex ?: 0
    }

    private fun List<String>.findHorizontalReflection(smudge: Boolean): Int {
        val horizontalIndex = (size - 1 downTo 1).firstOrNull { index ->
            isReflectionLine(this[index], this[index - 1], smudge) && isValid(index, smudge)
        }

        return horizontalIndex?.times(100) ?: 0
    }

    private fun List<String>.isValid(index: Int, smudge: Boolean): Boolean {
        val length = min(index, size - index)
        val firstHalf = subList(index - length, index).reversed()
        val secondHalf = subList(index, length + index)

        val errors = firstHalf.flatMapIndexed { y, line ->
            line.mapIndexed { x, c ->
                if (c != secondHalf[y][x]) 1 else 0
            }
        }.sum()

        return if (smudge) errors == 1 else errors == 0
    }

    private fun isReflectionLine(firstLine: String, secondLine: String, smudge: Boolean): Boolean {
        val errors = firstLine.mapIndexed { x, c -> if (secondLine[x] != c) 1 else 0 }.sum()

        return if (smudge) errors <= 1 else errors == 0
    }
}
