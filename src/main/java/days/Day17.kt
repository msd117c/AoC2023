package days

import utils.InputReader.readInput
import java.util.*


object Day17 {

    private enum class Direction(val coordinates: Coordinates) {
        RIGHT(Coordinates(x = 1, y = 0)),
        LEFT(Coordinates(x = -1, y = 0)),
        DOWN(Coordinates(x = 0, y = 1)),
        UP(Coordinates(x = 0, y = -1))
    }

    fun puzzle1() {
        readInput("day17Input") { lines ->
            val map = lines.toList().map { it.map { c -> c.digitToInt() }.toIntArray() }.toTypedArray()
            val start = Coordinates(x = 0, y = 0)
            val end = Coordinates(x = map.first().lastIndex, y = map.lastIndex)

            val (_, heatLoss) = map.dijkstra(start, end, isUltra = false)
            println("Day 17 puzzle 1 result is: $heatLoss")
        }
    }

    fun puzzle2() {
        readInput("day17Input") { lines ->
            val map = lines.toList().map { it.map { c -> c.digitToInt() }.toIntArray() }.toTypedArray()
            val start = Coordinates(x = 0, y = 0)
            val end = Coordinates(x = map.first().lastIndex, y = map.lastIndex)

            val (_, heatLoss) = map.dijkstra(start, end, isUltra = true)
            println("Day 17 puzzle 2 result is: $heatLoss")
        }
    }

    private fun Array<IntArray>.dijkstra(
        start: Coordinates,
        end: Coordinates,
        isUltra: Boolean
    ): Pair<List<Node>, Int> {
        val startNode = Node(start, steps = 0, Direction.RIGHT)
        val startState = State(startNode, cost = 0, path = emptyList())
        val queue = PriorityQueue<State>()
        queue.add(startState)
        val visited = mutableMapOf<Node, Boolean>()

        while (queue.isNotEmpty()) {
            val (currentNode, cost, path) = queue.remove()

            if (visited.containsKey(currentNode)) continue

            visited[currentNode] = true
            if (currentNode.coordinates == end) return path to cost

            val neighbors = findNeighbors(currentNode, isUltra)

            neighbors.forEach { neighbor ->
                val newCost = cost + this[neighbor.coordinates.y][neighbor.coordinates.x]

                val newPath = path + listOf(neighbor)
                val newState = State(neighbor, newCost, newPath)
                queue.add(newState)
            }
        }

        return emptyList<Node>() to 0
    }

    private fun Direction.left(): Direction {
        return when (this) {
            Direction.RIGHT -> Direction.UP
            Direction.LEFT -> Direction.DOWN
            Direction.DOWN -> Direction.RIGHT
            Direction.UP -> Direction.LEFT
        }
    }

    private fun Direction.right(): Direction {
        return when (this) {
            Direction.RIGHT -> Direction.DOWN
            Direction.LEFT -> Direction.UP
            Direction.DOWN -> Direction.LEFT
            Direction.UP -> Direction.RIGHT
        }
    }

    private fun Coordinates.add(direction: Direction): Coordinates {
        return Coordinates(x + direction.coordinates.x, y + direction.coordinates.y)
    }

    private fun Array<IntArray>.findNeighbors(currentNode: Node, isUltra: Boolean): List<Node> {
        val coordinates = currentNode.coordinates
        val steps = currentNode.steps
        val direction = currentNode.direction

        val nextNodes = if (isUltra) {
            val straight = if (steps < 10) {
                listOf(Node(coordinates.add(direction), steps = steps + 1, direction))
            } else emptyList()

            val turns = if (steps > 3) {
                listOf(
                    Node(coordinates.add(direction.left()), steps = 1, direction.left()),
                    Node(coordinates.add(direction.right()), steps = 1, direction.right()),
                )
            } else emptyList()

            straight + turns
        } else {
            if (steps < 3) {
                listOf(
                    Node(coordinates.add(direction), steps = steps + 1, direction),
                    Node(coordinates.add(direction.left()), steps = 1, direction.left()),
                    Node(coordinates.add(direction.right()), steps = 1, direction.right()),
                )
            } else {
                listOf(
                    Node(coordinates.add(direction.left()), steps = 1, direction.left()),
                    Node(coordinates.add(direction.right()), steps = 1, direction.right()),
                )
            }
        }

        return nextNodes
            .filter { nextNode ->
                val nextCoordinates = nextNode.coordinates

                nextCoordinates.x in 0..first().lastIndex && nextCoordinates.y in 0..lastIndex
            }
    }

    private fun Array<IntArray>.drawMap(coordinates: List<Node>) {
        indices.forEach { y ->
            first().indices.forEach { x ->
                if (coordinates.map { it.coordinates }.contains(Coordinates(x, y))) {
                    val movement = coordinates.first { it.coordinates == Coordinates(x, y) }
                    when (movement.direction) {
                        Direction.LEFT -> print('<')
                        Direction.RIGHT -> print('>')
                        Direction.UP -> print('^')
                        Direction.DOWN -> print('V')
                    }
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    private data class Node(val coordinates: Coordinates, val steps: Int, val direction: Direction)
    private data class State(val node: Node, val cost: Int, val path: List<Node>) : Comparable<State> {

        override fun compareTo(other: State): Int {
            var diff = cost - other.cost
            if (diff == 0 && node.direction == other.node.direction) {
                diff = node.steps - other.node.steps
            }
            if (diff == 0) {
                diff = (other.node.coordinates.y + other.node.coordinates.x) - (node.coordinates.y - node.coordinates.x)
            }
            return diff
        }
    }

    private data class Coordinates(val x: Int, val y: Int)
}
