package days

import utils.InputReader.readInput
import kotlin.math.abs

object Day11 {

    fun puzzle1() {
        readInput("day11Input") { lines ->
            val map = parseMap(lines.toList())
            val galaxies = getGalaxies(map)
            val galaxyPairs = getGalaxyPairs(galaxies)

            val emptyRows = findEmptyRows(map)
            val emptyColumns = findEmptyColumns(map)

            val distances = galaxyPairs.sumOf { (from, to) ->
                findShortestDistance(
                    from,
                    to,
                    emptyRows,
                    emptyColumns,
                    expansionFactor = 2
                )
            }
            println("Day 11 puzzle 1 result is: $distances")
        }
    }

    fun puzzle2() {
        readInput("day11Input") { lines ->
            val map = parseMap(lines.toList())
            val galaxies = getGalaxies(map)
            val galaxyPairs = getGalaxyPairs(galaxies)

            val emptyRows = findEmptyRows(map)
            val emptyColumns = findEmptyColumns(map)

            val distances = galaxyPairs.sumOf { (from, to) ->
                findShortestDistance(
                    from,
                    to,
                    emptyRows,
                    emptyColumns,
                    expansionFactor = 1000000
                )
            }
            println("Day 11 puzzle 2 result is: $distances")
        }
    }

    private fun parseMap(lines: List<String>): List<CharArray> {
        return lines.map { line -> line.toCharArray() }
    }

    private fun getGalaxies(map: List<CharArray>): List<Node> {
        return map.flatMapIndexed { y, line ->
            line.mapIndexed { x, symbol ->
                val coordinates = Coordinates(y, x)
                val isGalaxy = symbol == '#'

                Node(coordinates, isGalaxy)
            }
        }.filter { it.isGalaxy }
    }

    private fun getGalaxyPairs(galaxies: List<Node>): List<Pair<Node, Node>> {
        return galaxies.mapIndexed { index, galaxy ->
            galaxies.filterIndexed { i, _ -> i > index }.map { anotherGalaxy ->
                galaxy to anotherGalaxy
            }
        }.flatten()
    }

    private fun findEmptyRows(map: List<CharArray>): List<Int> {
        return map.mapIndexed { y, line -> if (line.all { symbol -> symbol == '.' }) y else null }.filterNotNull()
    }

    private fun findEmptyColumns(map: List<CharArray>): List<Int> {
        return map.first().mapIndexed { x, _ -> if (map.all { line -> line[x] == '.' }) x else null }.filterNotNull()
    }

    private fun findShortestDistance(
        startNode: Node,
        endNode: Node,
        emptyRows: List<Int>,
        emptyColumns: List<Int>,
        expansionFactor: Int,
    ): Long {
        val originalY = startNode.coordinates.y
        val originalX = startNode.coordinates.x

        val endY = endNode.coordinates.y
        val endX = endNode.coordinates.x

        val yDistance = abs(endY - originalY)
        val xDistance = abs(endX - originalX)

        val yRange = if (endY >= originalY) {
            (originalY until endY)
        } else {
            (endY until originalY)
        }
        val yOffset = yRange.count { y -> emptyRows.contains(y) }.toLong().times(expansionFactor - 1)

        val xRange = if (endX >= originalX) {
            (originalX until endX)
        } else {
            (endX until originalX)
        }
        val xOffset = xRange.count { x -> emptyColumns.contains(x) }.toLong().times(expansionFactor - 1)

        return yDistance + xDistance + yOffset + xOffset
    }

    private data class Coordinates(val y: Int, val x: Int)
    private data class Node(val coordinates: Coordinates, val isGalaxy: Boolean)
}
