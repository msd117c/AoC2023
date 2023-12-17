package days

import utils.InputReader.readInput
import kotlin.math.abs


object Day17 {

    private enum class Direction(val coordinates: Coordinates) {
        HORIZONTAL(Coordinates(x = 1, y = 0)),
        VERTICAL(Coordinates(x = 0, y = 1))
    }

    fun puzzle1() {
        readInput("day17Input") { lines ->
            val map = lines.toList().map { it.toCharArray() }.toTypedArray()
            val start = Coordinates(x = 0, y = 0)
            val end = Coordinates(x = map.first().lastIndex, y = map.lastIndex)

            val distances = map.dijkstra(start)
            map.drawMap(distances[end]!!.first)
            println(distances[end]!!.second)
        }
    }

    private fun Array<CharArray>.drawMap(coordinates: List<Movement>) {
        indices.forEach { y ->
            first().indices.forEach { x ->
                if (coordinates.map { it.coordinates }.contains(Coordinates(x, y))) {
                    val movement = coordinates.first { it.coordinates == Coordinates(x, y) }
                    when (movement.direction) {
                        Direction.HORIZONTAL -> print('>')
                        Direction.VERTICAL -> print('V')
                    }
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    private fun Array<CharArray>.dijkstra(start: Coordinates): Map<Coordinates, Pair<MutableList<Movement>, Int>> {
        val distances = mutableMapOf(start to 0)
        val paths = mutableMapOf(start to (mutableListOf<Movement>() to 0))
        val queue = mutableListOf(start to mutableListOf(Movement(start, Direction.HORIZONTAL)))

        while (queue.isNotEmpty()) {
            val (current, path) = queue.removeAt(0)

            val neighbors = findNeighbors(current, path)

            for (neighbor in neighbors) {
                val newDistance = distances[current]!! + neighbor.weight

                if (!distances.containsKey(neighbor.coordinates) || newDistance < distances[neighbor.coordinates]!!) {
                    distances[neighbor.coordinates] = newDistance

                    val newMovement = getMovement(current, neighbor.coordinates)
                    val newPath = neighbor.coordinates to (path + listOf(newMovement)).toMutableList()
                    queue.add(newPath)
                    paths[neighbor.coordinates] = (path + listOf(newMovement)).toMutableList() to newDistance
                }
            }
        }

        return paths
    }

    private fun getMovement(current: Coordinates, next: Coordinates): Movement {
        val dX = abs(current.x - next.x)
        val dY = abs(current.y - next.y)
        val dCoordinates = Coordinates(dX, dY)

        val direction = when (dCoordinates) {
            Direction.VERTICAL.coordinates -> Direction.VERTICAL
            Direction.HORIZONTAL.coordinates -> Direction.HORIZONTAL
            else -> error("No valid case")
        }

        return Movement(next, direction)
    }

    private fun Array<CharArray>.findNeighbors(coordinates: Coordinates, path: List<Movement>): List<Path> {
        val lastMovements = path.takeLast(3)

        val latest = path.lastOrNull()?.coordinates

        val nextOptions = if (lastMovements.size == 3 && lastMovements.map { it.direction }.distinct().size == 1) {
            when (lastMovements.map { it.direction }.last()) {
                Direction.VERTICAL -> {
                    listOf(
                        Coordinates(x = coordinates.x + 1, y = coordinates.y),
                        Coordinates(x = coordinates.x - 1, y = coordinates.y),
                    )
                }

                Direction.HORIZONTAL -> {
                    listOf(
                        Coordinates(x = coordinates.x, y = coordinates.y + 1),
                        Coordinates(x = coordinates.x, y = coordinates.y - 1),
                    )
                }
            }
        } else {
            listOf(
                Coordinates(x = coordinates.x + 1, y = coordinates.y),
                Coordinates(x = coordinates.x - 1, y = coordinates.y),
                Coordinates(x = coordinates.x, y = coordinates.y + 1),
                Coordinates(x = coordinates.x, y = coordinates.y - 1),
            )
        }

        return nextOptions
            .filter { neighborCoordinates ->
                val x = neighborCoordinates.x
                val y = neighborCoordinates.y

                y in 0..lastIndex && x in 0..first().lastIndex
            }
            .filterNot { it == latest }
            .map { neighborCoordinates ->
                val weight = this[neighborCoordinates.y][neighborCoordinates.x].toString().toInt()

                Path(neighborCoordinates, weight)
            }
    }

    private data class Path(val coordinates: Coordinates, val weight: Int)
    private data class Movement(val coordinates: Coordinates, val direction: Direction)
    private data class Coordinates(val x: Int, val y: Int)
}
