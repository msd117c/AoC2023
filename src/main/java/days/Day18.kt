package days

import utils.InputReader.readInput

object Day18 {

    private const val INSTRUCTION_REGEX = "^(.) (\\d+) \\(#(.*)\\)"

    private enum class Direction(val coordinates: Coordinates) {
        UP(Coordinates(x = 0, y = -1)), DOWN(Coordinates(x = 0, y = 1)), RIGHT(Coordinates(x = 1, y = 0)), LEFT(
            Coordinates(x = -1, y = 0)
        ),
    }

    @JvmStatic
    fun main(args: Array<String>) {
        puzzle1()
        puzzle2()
    }

    fun puzzle1() {
        readInput("day18Input") { lines ->
            val instructions = lines.toList().map { it.parseInstruction() }.flatten()
            val start = Node(Coordinates(x = 0, y = 0))
            val toDigList = instructions.execute(start).distinctBy { it.coordinates }
            val minX = toDigList.minBy { it.coordinates.x }.coordinates.x
            val minY = toDigList.minBy { it.coordinates.y }.coordinates.y

            val offsetX = 0 - minX + 1
            val offsetY = 0 - minY + 1

            val fixedList = toDigList.map {
                it.copy(
                    coordinates = it.coordinates.copy(
                        x = it.coordinates.x + offsetX, y = it.coordinates.y + offsetY
                    )
                )
            }

            val innerArea = findInnerArea(fixedList.map { it.coordinates })
            fixedList.drawMap(innerArea)
            println("Day 18 puzzle 1 result is: ${fixedList.size + innerArea.size}")
        }
    }

    private fun String.parseInstruction(): List<Instruction> {
        val groups = Regex(INSTRUCTION_REGEX).findAll(this).toList()

        val directionValue = groups[0].groups[1]?.value ?: error("No valid direction")

        val direction = when (directionValue) {
            "U" -> Direction.UP
            "D" -> Direction.DOWN
            "R" -> Direction.RIGHT
            "L" -> Direction.LEFT
            else -> error("No valid direction value")
        }
        val steps = groups[0].groups[2]?.value?.toInt() ?: error("No valid steps")

        return Array(steps) { Instruction(direction) }.toList()
    }

    private fun puzzle2() {
        readInput("day18Input") { lines ->
            val instructions = lines.toList().map { it.parseInstruction2() }.flatten()
            val start = Node(Coordinates(x = 0, y = 0))
            val toDigList = instructions.execute(start).distinctBy { it.coordinates }
            val minX = toDigList.minBy { it.coordinates.x }.coordinates.x
            val minY = toDigList.minBy { it.coordinates.y }.coordinates.y

            val offsetX = 0 - minX + 1
            val offsetY = 0 - minY + 1

            val fixedList = toDigList.map {
                it.copy(
                    coordinates = it.coordinates.copy(
                        x = it.coordinates.x + offsetX, y = it.coordinates.y + offsetY
                    )
                )
            }

            val innerArea = findInnerArea(fixedList.map { it.coordinates })
            fixedList.drawMap(innerArea)
            println("Day 18 puzzle 2 result is: ${fixedList.size + innerArea.size}")
        }
    }

    private fun String.parseInstruction2(): List<Instruction> {
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

        return Array(steps.toInt()) { Instruction(direction) }.toList()
    }

    private fun List<Instruction>.execute(start: Node): List<Node> {
        var current = start

        return listOf(start) + map { instruction ->
            current = current.add(instruction.direction)
            current
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

    private fun List<Node>.drawMap(area: List<Coordinates>) {
        val maxX = maxBy { it.coordinates.x }.coordinates.x + 1
        val minX = minBy { it.coordinates.x }.coordinates.x
        val width = maxX - minX + 2

        val maxY = maxBy { it.coordinates.y }.coordinates.y + 1
        val minY = minBy { it.coordinates.y }.coordinates.y
        val height = maxY - minY + 2

        val map = Array(height) { CharArray(width) { '.' } }

        map.indices.forEach { y ->
            map.first().indices.forEach { x ->
                when {
                    area.contains(Coordinates(x, y)) -> print('O')
                    map { it.coordinates.x to it.coordinates.y }.contains(x to y) -> print('#')
                    else -> print('.')
                }
            }
            println()
        }
    }

    private fun Node.add(direction: Direction): Node {
        return Node(
            Coordinates(
                x = coordinates.x + direction.coordinates.x, y = coordinates.y + direction.coordinates.y
            )
        )
    }

    private data class Instruction(val direction: Direction)
    private data class Node(val coordinates: Coordinates)
    private data class Coordinates(val x: Int, val y: Int)
}
