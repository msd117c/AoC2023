package days

import utils.InputReader.readInput
import kotlin.math.min

object Day13 {

    fun puzzle1() {
        readInput("day13Input") { lines ->
            val blocks = parseBlocks(lines.toList())

            val result = blocks.sumOf { block -> findReflections(block) }
            println("Day 13 puzzle 1 result is: $result")
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

    private fun findReflections(block: List<String>): Int {
        val horizontal = findHorizontalReflection(block)
        if (horizontal != 0) {
            println(horizontal)
            return horizontal
        }

        val vertical = findVerticalReflection(block)
        if (vertical != 0) {
            println(vertical)
            return vertical
        }

        return 0
    }

    private fun findVerticalReflection(block: List<String>): Int {
        val columns = block.first().mapIndexed { index, _ ->
            block.map { line -> line[index] }.joinToString("")
        }

        var verticalIndex = (columns.size - 1 downTo 1).firstOrNull { index ->
            columns[index] == columns[index - 1] && columns.isValid(index)
        }

        if (verticalIndex != null) return verticalIndex

        verticalIndex = (0 until  columns.size - 1).firstOrNull { index ->
            columns[index] == columns[index + 1] && columns.isValid(index)
        }?.plus(1)

        return verticalIndex ?: 0
    }

    private fun findHorizontalReflection(block: List<String>): Int {
        var horizontalIndex = (block.size - 1 downTo  1).firstOrNull { index ->
            block[index] == block[index - 1] && block.isValid(index)
        }

        if (horizontalIndex != null) return horizontalIndex.times(100)

        horizontalIndex = (0 until  block.size - 1).firstOrNull { index ->
            block[index] == block[index + 1] && block.isValid(index)
        }?.plus(1)

        return horizontalIndex?.times(100) ?: 0
    }

    private fun List<String>.isValid(index: Int): Boolean {
        val firstHalf = subList(0, index).reversed()
        val secondHalf = subList(index, size)
        val length = min(firstHalf.size, secondHalf.size)

        return firstHalf.subList(0, length) == secondHalf.subList(0, length)
    }
}
