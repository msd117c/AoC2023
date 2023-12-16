package days

import utils.InputReader.readInput
import kotlin.math.min

object Day13 {

    fun puzzle1() {
        readInput("day13Input") { lines ->
            val blocks = parseBlocks(lines.toList())

            val result = blocks.sumOf { block -> block.findReflections(smudge = false) }
            println("Day 13 puzzle 1 result is: $result")
        }
    }

    fun puzzle2() {
        readInput("day13Input") { lines ->
            val blocks = parseBlocks(lines.toList())

            val result = blocks.sumOf { block -> block.findReflections(true) }
            println("Day 13 puzzle 2 result is: $result")
        }
    }

    private fun parseBlocks(lines: List<String>): List<List<String>> {
        val blocks = mutableListOf(mutableListOf<String>())

        lines.forEach { line ->
            if (line.isEmpty()) {
                blocks.add(mutableListOf())
            } else {
                blocks.last().add(line)
            }
        }

        return blocks
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
        val firstHalf = subList(0, index).reversed()
        val secondHalf = subList(index, size)
        val length = min(firstHalf.size, secondHalf.size)

        val firstReflected = firstHalf.subList(0, length)
        val secondReflected = secondHalf.subList(0, length)

        val errors = firstReflected.flatMapIndexed { y, line ->
            line.mapIndexed { x, c ->
                if (c != secondReflected[y][x]) 1 else 0
            }
        }.sum()

        return if (smudge) errors == 1 else errors == 0
    }

    private fun isReflectionLine(firstLine: String, secondLine: String, smudge: Boolean): Boolean {
        val errors = firstLine.mapIndexed { x, c -> if (secondLine[x] != c) 1 else 0 }.sum()

        return if (smudge) errors <= 1 else errors == 0
    }
}
