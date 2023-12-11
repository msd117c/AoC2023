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
            val newMap = zoomMap(map)
            val graph = parseGraph(newMap)
            val start = getStartPosition(newMap)

            val mainLoop = findMainLoop(graph, start)
            val nonMainLoop = findNonMainLoop(graph, mainLoop)
            val outerLoops = findOuterLoops(nonMainLoop, newMap).flatten()
            val enclosedTiles = nonMainLoop.minus(outerLoops.toSet())
            val reducedEnclosedTiles = enclosedTiles
                .filterNot { coordinates -> coordinates.x % 2 != 0 }
                .filterNot { coordinates -> coordinates.y % 2 != 0 }

            println("Day 10 puzzle 2 result is: ${reducedEnclosedTiles.size}")
        }
    }

    private fun zoomMap(map: Array<CharArray>): Array<CharArray> {
        val newMap = mutableListOf<CharArray>()

        map.forEachIndexed { y, line ->
            val newFirstLine = line.mapIndexed { x, symbol ->
                when (symbol) {
                    '|' -> "|."
                    '-' -> "--"
                    'L' -> "L-"
                    'J' -> "J."
                    '7' -> "7."
                    'F' -> "F-"
                    'S' -> {
                        when (line[x + 1]) {
                            '-' -> "S-"
                            'J' -> "S-"
                            '7' -> "S-"
                            else -> "S."
                        }
                    }

                    '.' -> ".."
                    else -> throw IllegalArgumentException("Not valid tile")
                }
            }.joinToString("")

            val newSecond = newFirstLine.mapIndexed { x, symbol ->
                when (symbol) {
                    '|' -> '|'
                    '-' -> '.'
                    'L' -> '.'
                    'J' -> '.'
                    '7' -> '|'
                    'F' -> '|'
                    'S' -> {
                        when (map[y + 1][x.div(2)]) {
                            '|' -> '|'
                            'J' -> '|'
                            'L' -> '|'
                            else -> '.'
                        }
                    }

                    '.' -> '.'
                    else -> throw IllegalArgumentException("Not valid tile")
                }
            }

            newMap.add(newFirstLine.toCharArray())
            newMap.add(newSecond.toCharArray())
        }



        return newMap.toTypedArray()
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

    private fun findNonMainLoop(graph: Map<Coordinates, List<Path>>, mainLoop: List<Coordinates>): List<Coordinates> {
        return graph.keys.toList().minus(mainLoop.toSet())
    }

    private fun findOuterLoops(nonMainLoop: List<Coordinates>, map: Array<CharArray>): List<List<Coordinates>> {
        val edgeCoordinates = nonMainLoop.filter { coordinates ->
            coordinates.x == 0 || coordinates.y == 0 ||
                    coordinates.x == map.first().size - 1 || coordinates.y == map.size - 1
        }.toMutableList()
        val outerLoops = mutableListOf(listOf(edgeCoordinates.first()))

        while (edgeCoordinates.isNotEmpty()) {
            outerLoops.add(discoverOuterLoops(nonMainLoop, edgeCoordinates.removeAt(0)))
            edgeCoordinates.removeAll { coordinates -> outerLoops.any { it.contains(coordinates) } }
        }

        return outerLoops
    }

    private fun findNeighbors(coordinates: Coordinates, availableCoordinates: List<Coordinates>): List<Coordinates> {
        val y = coordinates.y
        val x = coordinates.x

        return listOf(
            Coordinates(y + 1, x),
            Coordinates(y - 1, x),
            Coordinates(y, x + 1),
            Coordinates(y, x - 1),
        ).filter { neighborCoordinates ->
            neighborCoordinates.x >= 0 && neighborCoordinates.y >= 0
        }.filter { neighborCoordinates ->
            availableCoordinates.contains(neighborCoordinates)
        }
    }

    private fun discoverOuterLoops(nonMainLoop: List<Coordinates>, start: Coordinates): List<Coordinates> {
        val coordinates = mutableListOf(start)
        val queue = mutableListOf(start)

        while (queue.isNotEmpty()) {
            val current = queue.removeAt(0)

            val neighbors = findNeighbors(current, nonMainLoop)
            for (neighbor in neighbors) {
                if (!coordinates.contains(neighbor)) {
                    coordinates.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }

        return coordinates
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
