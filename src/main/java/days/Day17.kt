package days

import utils.InputReader.readInput


object Day17 {

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
    ): Map<Coordinates, Pair<List<Coordinates>, Int>> {
        val distances = mutableMapOf(start to 0)
        val queue = mutableListOf(start to mutableListOf(start))

        while (queue.isNotEmpty()) {
            val (current, path) = queue.removeAt(0)

            val neighbors = findNeighbors(current, path.take(path.size - 1))

            for (neighbor in neighbors) {
                val newDistance = distances[current]!! + neighbor.weight
                if (neighbor.coordinates == end) return mutableMapOf(end to (path + listOf(end) to newDistance))

                if (!distances.containsKey(neighbor.coordinates) || newDistance < distances[neighbor.coordinates]!!) {
                    distances[neighbor.coordinates] = newDistance

                    val newPath = neighbor.coordinates to (path + listOf(neighbor.coordinates)).toMutableList()
                    queue.add(newPath)
                }
            }
        }

        return emptyMap()
    }

    private fun Array<CharArray>.findNeighbors(coordinates: Coordinates, path: List<Coordinates>): List<Path> {
        val lastThree = path.takeLast(2)
        val latest = path.lastOrNull()

        val nextOptions = if (lastThree.size == 2) {
            when {
                lastThree.map { it.x }.distinct().size == 1 -> {
                    listOf(
                        Coordinates(x = coordinates.x + 1, y = coordinates.y),
                        Coordinates(x = coordinates.x - 1, y = coordinates.y),
                    )
                }

                lastThree.map { it.y }.distinct().size == 1 -> {
                    listOf(
                        Coordinates(x = coordinates.x, y = coordinates.y + 1),
                        Coordinates(x = coordinates.x, y = coordinates.y - 1),
                    )
                }

                else -> {
                    listOf(
                        Coordinates(x = coordinates.x + 1, y = coordinates.y),
                        Coordinates(x = coordinates.x - 1, y = coordinates.y),
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
