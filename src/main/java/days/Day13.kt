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

        var verticalIndex = (columns.size - 1 downTo 1).firstOrNull { index ->
            isValid(columns[index], columns[index - 1], smudge) && columns.isValid(index, smudge)
        }

        if (verticalIndex != null) return verticalIndex

        verticalIndex = (0 until columns.size - 1).firstOrNull { index ->
            isValid(columns[index], columns[index + 1], smudge) && columns.isValid(index, smudge)
        }?.plus(1)

        return verticalIndex ?: 0
    }

    private fun List<String>.findHorizontalReflection(smudge: Boolean): Int {
        var horizontalIndex = (size - 1 downTo 1).firstOrNull { index ->
            isValid(this[index], this[index - 1], smudge) && isValid(index, smudge)
        }

        if (horizontalIndex != null) return horizontalIndex.times(100)

        horizontalIndex = (0 until size - 1).firstOrNull { index ->
            isValid(this[index], this[index + 1], smudge) && isValid(index, smudge)
        }?.plus(1)

        return horizontalIndex?.times(100) ?: 0
    }

    private fun List<String>.isValid(index: Int, smudge: Boolean): Boolean {
        val firstHalf = subList(0, index).reversed()
        val secondHalf = subList(index, size)
        val length = min(firstHalf.size, secondHalf.size)

        var errors = 0
        firstHalf.subList(0, length).forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c != secondHalf.subList(0, length)[y][x]) errors++
            }
        }
        return if (smudge) errors == 1 else errors == 0
    }

    private fun isValid(firstLine: String, secondLine: String, smudge: Boolean): Boolean {
        var errors = 0
        firstLine.forEachIndexed { x, c -> if (secondLine[x] != c) errors++ }

        return if (smudge) errors <= 1 else errors == 0
    }
}
