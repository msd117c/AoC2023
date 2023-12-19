package days

import utils.InputReader.readInput
import kotlin.math.abs

object Day18 {

    private const val INSTRUCTION_REGEX = "^(.) (\\d+) \\(#(.*)\\)"

    private enum class Direction(val coordinates: Coordinates) {
        UP(Coordinates(x = 0, y = -1)),
        DOWN(Coordinates(x = 0, y = 1)),
        RIGHT(Coordinates(x = 1, y = 0)),
        LEFT(Coordinates(x = -1, y = 0)),
    }

    @JvmStatic
    fun main(args: Array<String>) {
        puzzle1()
        puzzle2()
    }

    fun puzzle1() {
        readInput("day18Input") { lines ->
            val instructions = lines.toList().map { it.parseDigPlan() }
            val vertices = instructions.executeDigPlan()

            val result = lavaCapacity(vertices)
            println("Day 18 puzzle 1 result is: $result")
        }
    }

    private fun String.parseDigPlan(): Instruction {
        val groups = Regex(INSTRUCTION_REGEX).findAll(this).toList()

        val directionValue = groups[0].groups[1]?.value ?: error("No valid direction")

        val direction = when (directionValue) {
            "U" -> Direction.UP
            "D" -> Direction.DOWN
            "R" -> Direction.RIGHT
            "L" -> Direction.LEFT
            else -> error("No valid direction value")
        }
        val steps = groups[0].groups[2]?.value?.toLong() ?: error("No valid steps")

        return Instruction(direction, steps)
    }

    private fun puzzle2() {
        readInput("day18Input") { lines ->
            val input = lines.toList()
            val instructions = input.map { it.parseColorDigPlan() }
            val vertices = instructions.executeDigPlan()

            val capacity = lavaCapacity(vertices)
            println("Day 18 puzzle 2 result is: $capacity")
        }
    }

    private fun String.parseColorDigPlan(): Instruction {
        val groups = Regex(INSTRUCTION_REGEX).findAll(this).toList()

        val hex = groups[0].groups[3]?.value ?: error("No valid hex")
        val direction = when (hex.last()) {
            '3' -> Direction.UP
            '1' -> Direction.DOWN
            '0' -> Direction.RIGHT
            '2' -> Direction.LEFT
            else -> error("No valid direction value")
        }
        val steps = hex.dropLast(1).toLong(radix = 16)

        return Instruction(direction, steps)
    }

    private fun List<Instruction>.executeDigPlan(): List<Coordinates> {
        return map { instruction -> Coordinates(x = 0, y = 0).execute(instruction) }
    }

    private fun lavaCapacity(digPlan: List<Coordinates>): Long {
        return integerPointsFromPickTheorem(shoelaceFormula(cornerPositions(digPlan)), perimeter(digPlan))
    }

    private fun integerPointsFromPickTheorem(area: Long, perimeter: Long): Long = area + perimeter / 2L + 1L

    private fun shoelaceFormula(corners: List<Coordinates>): Long {
        return corners.zipWithNext { a, b -> a.x * b.y - a.y * b.x }.sum() / 2
    }

    private fun cornerPositions(digPlan: List<Coordinates>): List<Coordinates> {
        return digPlan.scan(Coordinates(0, 0)) { acc, shiftVector ->
            acc.add(shiftVector)
        }
    }

    private fun perimeter(digPlan: List<Coordinates>): Long = digPlan.sumOf { abs(it.x) + abs(it.y) }

    private data class Instruction(val direction: Direction, val steps: Long)
    private data class Coordinates(val x: Long, val y: Long) {

        fun add(coordinates: Coordinates): Coordinates {
            return copy(x = x + coordinates.x, y = y + coordinates.y)
        }

        fun execute(instruction: Instruction): Coordinates {
            val direction = instruction.direction
            val multiplier = instruction.steps

            return copy(
                x = (x + direction.coordinates.x).times(multiplier),
                y = (y + direction.coordinates.y).times(multiplier)
            )
        }
    }
}
