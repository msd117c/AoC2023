package days

import utils.InputReader.readInput

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
            val instructions = lines.toList().map { it.parseInstruction() }
            val start = Coordinates(x = 0, y = 0)
            val toDigList = instructions.execute(start).distinct()
            val minX = toDigList.minBy { it.x }.x
            val minY = toDigList.minBy { it.y }.y

            val offsetX = 0 - minX + 1
            val offsetY = 0 - minY + 1

            val fixedList = toDigList.map { it.copy(x = it.x + offsetX, y = it.y + offsetY) }

            val innerArea = findInnerArea(fixedList)
            fixedList.drawMap(innerArea)
            println("Day 18 puzzle 1 result is: ${fixedList.size + innerArea.size}")
        }
    }

    private fun String.parseInstruction(): Instruction {
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
            val instructions1 = input.map { it.parseInstruction() }
            val instructions2 = input.map { it.parseInstruction2() }.mapIndexed { index, instruction ->
                instruction.copy(steps = instruction.steps.div(instructions1[index].steps))
            }
            val start = Coordinates(x = 0, y = 0)
            val toDigList = instructions2.execute(start).distinct()
            val minX = toDigList.minBy { it.x }.x
            val minY = toDigList.minBy { it.y }.y

            val offsetX = 0 - minX + 1
            val offsetY = 0 - minY + 1

            val fixedList = toDigList.map { it.copy(x = it.x + offsetX, y = it.y + offsetY) }

            val innerArea = findInnerArea(fixedList)
            fixedList.drawMap(innerArea)
            println("Day 18 puzzle 2 result is: ${fixedList.size + innerArea.size}")
        }
    }

    private fun calculateGCDForListOfNumbers(numbers: List<Long>): Long {
        require(numbers.isNotEmpty()) { "List must not be empty" }
        var result = numbers[0]
        for (i in 1 until numbers.size) {
            var num1 = result
            var num2 = numbers[i]
            while (num2 != 0L) {
                val temp = num2
                num2 = num1 % num2
                num1 = temp
            }
            result = num1
        }
        return result
    }


    private fun String.parseInstruction2(): Instruction {
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

    private fun List<Instruction>.execute(start: Coordinates): List<Coordinates> {
        var current = start

        return listOf(start) + flatMap { instruction ->
            (0 until instruction.steps).map {
                current = current.add(instruction.direction)
                current
            }
        }
    }

    private fun findInnerArea(perimeter: List<Coordinates>): List<Coordinates> {
        val start = perimeter.findFirstInnerPoint()
        val visited = mutableListOf(start)
        val queue = mutableListOf(start)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            val neighbors = listOf(
                Coordinates(x = current.x + 1, y = current.y),
                Coordinates(x = current.x - 1, y = current.y),
                Coordinates(x = current.x, y = current.y + 1),
                Coordinates(x = current.x, y = current.y - 1),
            ).filterNot { perimeter.contains(it) }.filter { coordinates ->
                coordinates.x > 0 && coordinates.y > 0
            }

            neighbors.forEach { neighbor ->
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }

        return visited
    }

    private fun List<Coordinates>.findFirstInnerPoint(): Coordinates {
        val columns = map { it.x }.distinct()

        columns.forEach { x ->
            val rows = filter { it.x == x }.map { it.y }

            rows.windowed(size = 2, step = 1).forEach {
                val first = it.min()
                val last = it.max()

                ((first + 1) until last).map { y ->
                    return Coordinates(x, y)
                }
            }
        }

        error("No inner point found!")
    }

    private fun List<Coordinates>.drawMap(area: List<Coordinates>) {
        val maxX = maxBy { it.x }.x + 1
        val minX = minBy { it.x }.x
        val width = maxX - minX + 2

        val maxY = maxBy { it.y }.y + 1
        val minY = minBy { it.y }.y
        val height = maxY - minY + 2

        val map = Array(height) { CharArray(width) { '.' } }

        map.indices.forEach { y ->
            map.first().indices.forEach { x ->
                when {
                    area.contains(Coordinates(x, y)) -> print('O')
                    map { it.x to it.y }.contains(x to y) -> print('#')
                    else -> print('.')
                }
            }
            println()
        }
    }

    private fun Coordinates.add(direction: Direction): Coordinates {
        return Coordinates(x = x + direction.coordinates.x, y = y + direction.coordinates.y)
    }

    private data class Instruction(val direction: Direction, val steps: Long)
    private data class Coordinates(val x: Int, val y: Int)
}
