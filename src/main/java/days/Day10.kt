package days

import utils.InputReader.readInput

object Day10 {

    fun puzzle1() {
        readInput("day10Input") { lines ->
            val map = parseMap(lines.toList())
            val graph = parseGraph(map)
            val start = getStartPosition(map)

            val distances = dijkstra(graph, start)
            println("Day 10 puzzle 1 result is: ${distances.values.max()}")
        }
    }

    private fun dijkstra(graph: Map<Coordinates, List<Path>>, start: Coordinates): Map<Coordinates, Int> {
        val distances = mutableMapOf(start to 0)
        val visited = mutableSetOf(start)
        val queue = mutableListOf(start)

        while (queue.isNotEmpty()) {
            val current = queue.removeAt(0)

            for (neighbor in graph[current] ?: emptyList()) {
                val newDistance = distances[current]!! + neighbor.weight

                if (!distances.containsKey(neighbor.coordinates) || newDistance < distances[neighbor.coordinates]!!) {
                    distances[neighbor.coordinates] = newDistance
                    queue.add(neighbor.coordinates)
                }
            }

            visited.add(current)
        }

        return distances
    }

    fun puzzle2() {
        readInput("day10Input") { lines ->
            val map = parseMap(lines.toList())
            val graph = parseGraph(map)
            val start = getStartPosition(map)

            val mainLoop = findMainLoop(graph, start)
            val enclosedTiles = findEnclosedTiles(mainLoop, map)
            drawMap(map, mainLoop, enclosedTiles)
            //println("Day 10 puzzle 2 result is: ${distances.values.max()}")
        }
    }

    private fun findMainLoop(graph: Map<Coordinates, List<Path>>, start: Coordinates): List<Coordinates> {
        val mainLoop = mutableListOf(start)
        var isClosed = false

        while (!isClosed) {
            val nextCoordinates =
                graph[mainLoop.last()]!!.firstOrNull { !mainLoop.contains(it.coordinates) }?.coordinates
            if (nextCoordinates == null) {
                isClosed = true
            } else {
                mainLoop.add(nextCoordinates)
            }
        }

        return mainLoop
    }

    private fun findEnclosedTiles(mainLoop: List<Coordinates>, map: Array<CharArray>): List<Coordinates> {
        return map.flatMapIndexed { y, line ->
            line.mapIndexed { x, _ ->
                val coordinates = Coordinates(y, x)
                if (isEnclosed(coordinates, mainLoop, map)) {
                    coordinates
                } else {
                    null
                }
            }
        }.filterNotNull()
    }

    private fun isEnclosed(coordinates: Coordinates, mainLoop: List<Coordinates>, map: Array<CharArray>): Boolean {
        val isPartOfMainLoop = mainLoop.contains(coordinates)

        val y = coordinates.y
        val x = coordinates.x

        val isEnclosedHorizontally = (0 until x + 1).any { mainLoop.contains(Coordinates(y, it)) } &&
                (x + 1 until map.first().size).any { mainLoop.contains(Coordinates(y, it)) }
        val isEnclosedVertically = (0 until y + 1).any { mainLoop.contains(Coordinates(it, x)) } &&
                (y + 1 until map.size).any { mainLoop.contains(Coordinates(it, x)) }

        return !isPartOfMainLoop && isEnclosedHorizontally && isEnclosedVertically
    }

    private fun drawMap(map: Array<CharArray>, mainLoop: List<Coordinates>, enclosedTiles: List<Coordinates>) {
        map.forEachIndexed { y, line ->
            val updatedLine = line.mapIndexed { x, symbol ->
                val coordinates = Coordinates(y, x)

                when {
                    //mainLoop.contains(coordinates) -> 'X'
                    enclosedTiles.contains(coordinates) -> 'I'
                    else -> symbol
                }
            }
            println(updatedLine)
        }
    }

    private fun parseMap(lines: List<String>): Array<CharArray> {
        return lines.map { line -> line.toCharArray() }.toTypedArray()
    }

    private fun parseGraph(map: Array<CharArray>): Map<Coordinates, List<Path>> {
        return map.flatMapIndexed { y, line ->
            line.mapIndexed { x, symbol ->
                val coordinates = Coordinates(y, x)

                coordinates to findNeighbors(coordinates, symbol, map)
            }
        }.toMap()
    }

    private fun findNeighbors(coordinates: Coordinates, symbol: Char, map: Array<CharArray>): List<Path> {
        val y = coordinates.y
        val x = coordinates.x

        return when (symbol) {
            '|' -> listOf(Coordinates(y + 1, x), Coordinates(y - 1, x))
            '-' -> listOf(Coordinates(y, x + 1), Coordinates(y, x - 1))
            'L' -> listOf(Coordinates(y, x + 1), Coordinates(y - 1, x))
            'J' -> listOf(Coordinates(y, x - 1), Coordinates(y - 1, x))
            '7' -> listOf(Coordinates(y, x - 1), Coordinates(y + 1, x))
            'F' -> listOf(Coordinates(y, x + 1), Coordinates(y + 1, x))
            'S' -> {
                val left = Coordinates(y, x - 1)
                val right = Coordinates(y, x + 1)
                val top = Coordinates(y - 1, x)
                val down = Coordinates(y + 1, x)

                listOf(left, right, top, down)
                    .filter { neighborCoordinates ->
                        neighborCoordinates.y >= 0 && neighborCoordinates.x >= 0 &&
                                neighborCoordinates.y < map.size && neighborCoordinates.x < map.first().size
                    }
                    .filter { neighborCoordinates ->
                        val neighborSymbol = map[neighborCoordinates.y][neighborCoordinates.x]

                        when (neighborCoordinates) {
                            left -> neighborSymbol == '-' || neighborSymbol == 'L' || neighborSymbol == 'F'
                            right -> neighborSymbol == '-' || neighborSymbol == '7' || neighborSymbol == 'J'
                            top -> neighborSymbol == '|' || neighborSymbol == '7' || neighborSymbol == 'F'
                            down -> neighborSymbol == '|' || neighborSymbol == 'L' || neighborSymbol == 'J'
                            else -> false
                        }
                    }
            }

            else -> emptyList()
        }.filter { neighborCoordinates ->
            neighborCoordinates.y >= 0 && neighborCoordinates.x >= 0 &&
                    neighborCoordinates.y < map.size && neighborCoordinates.x < map.first().size
        }.map { neighborCoordinates -> Path(neighborCoordinates, weight = 1) }
    }

    private fun getStartPosition(map: Array<CharArray>): Coordinates {
        return map.flatMapIndexed { y, line ->
            line.mapIndexed { x, symbol -> if (symbol == 'S') Coordinates(y, x) else null }
        }.filterNotNull().first()
    }

    private data class Coordinates(val y: Int, val x: Int)
    private data class Path(val coordinates: Coordinates, val weight: Int)
}
