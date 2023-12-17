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

            val distances = map.dijkstra(start, end)
            map.drawMap(distances[end]!!.first)
            println(distances[end]!!.second)
        }
    }

    private fun Array<CharArray>.drawMap(coordinates: List<Coordinates>) {
        indices.forEach { y ->
            first().indices.forEach { x ->
                if (coordinates.contains(Coordinates(x, y))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    private fun Array<CharArray>.dijkstra(
        start: Coordinates,
        end: Coordinates
    ): Map<Coordinates, Pair<MutableList<Coordinates>, Int>> {
        val distances = mutableMapOf(start to 0)
        val paths = mutableMapOf(start to (mutableListOf<Coordinates>() to 0))
        val queue = mutableListOf(start to mutableListOf(start))

        while (queue.isNotEmpty()) {
            val (current, path) = queue.removeAt(0)

            val neighbors = findNeighbors(current, path.dropLast(1))

            for (neighbor in neighbors) {
                val newDistance = distances[current]!! + neighbor.weight
                //if (neighbor.coordinates == end) return mutableMapOf(end to (path + listOf(end) to newDistance))

                if (!distances.containsKey(neighbor.coordinates) || newDistance < distances[neighbor.coordinates]!!) {
                    distances[neighbor.coordinates] = newDistance

                    val newPath = neighbor.coordinates to (path + listOf(neighbor.coordinates)).toMutableList()
                    queue.add(newPath)
                    paths[neighbor.coordinates] = (path + listOf(neighbor.coordinates)).toMutableList() to newDistance
                }
            }
        }

        //return emptyMap()
        return paths
    }

    private fun Array<CharArray>.findNeighbors(coordinates: Coordinates, path: List<Coordinates>): List<Path> {
        val lastFour = path.takeLast(4)
        val lastDirections = if (lastFour.size == 4) {
            lastFour.indices.mapNotNull { index ->
                if (index == 0) {
                    null
                } else {
                    val dX = abs(lastFour[index].x - lastFour[index - 1].x)
                    val dY = abs(lastFour[index].y - lastFour[index - 1].y)
                    val dCoordinates = Coordinates(dX, dY)

                    when (dCoordinates) {
                        Direction.VERTICAL.coordinates -> Direction.VERTICAL
                        Direction.HORIZONTAL.coordinates -> Direction.HORIZONTAL
                        else -> error("No valid case")
                    }
                }
            }
        } else {
            emptyList()
        }

        val latest = path.lastOrNull()

        val nextOptions = if (lastDirections.size == 3 && lastDirections.distinct().size == 1) {
            when (lastDirections.last()) {
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
    private data class Coordinates(val x: Int, val y: Int)
}
