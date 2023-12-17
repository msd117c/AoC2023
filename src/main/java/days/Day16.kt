package days

import utils.InputReader.readInput

object Day16 {

    private enum class Direction(val coordinates: Coordinates) {
        UP(Coordinates(x = 0, y = -1)),
        DOWN(Coordinates(x = 0, y = 1)),
        RIGHT(Coordinates(x = 1, y = 0)),
        LEFT(Coordinates(x = -1, y = 0))
    }

    fun puzzle1() {
        readInput("day16Input") { lines ->
            val map = lines.toList().map { it.toCharArray() }.toTypedArray()

            val start = Coordinates(x = 0, y = 0)
            val direction = Direction.RIGHT
            val visitedTiles = mutableMapOf((start to direction) to true)

            map.moveBeam(start, direction, visitedTiles)

            val energizedTiles = visitedTiles.keys.distinctBy { it.first }.size
            println("Day 16 puzzle 1 result is: $energizedTiles")
        }
    }

    fun puzzle2() {
        readInput("day16Input") { lines ->
            val map = lines.toList().map { it.toCharArray() }.toTypedArray()

            val energizedTiles = map.tryAllStartingPoints()
            println("Day 16 puzzle 2 result is: $energizedTiles")
        }
    }

    private fun Array<CharArray>.tryAllStartingPoints(): Int {
        val startingPointsAndDirections = (first().mapIndexed { x, _ ->
            Coordinates(x, y = 0) to Direction.DOWN
        } + last().mapIndexed { x, _ ->
            Coordinates(x, y = lastIndex) to Direction.UP
        } + mapIndexed { y, _ ->
            Coordinates(x = 0, y) to Direction.RIGHT
        } + mapIndexed { y, _ ->
            Coordinates(x = first().lastIndex, y) to Direction.LEFT
        }).toMutableList()

        var energizedTiles = 0
        while (startingPointsAndDirections.isNotEmpty()) {
            val (start, direction) = startingPointsAndDirections.removeAt(0)
            val visitedTiles = mutableMapOf(start to Direction.RIGHT to true)

            moveBeam(start, direction, visitedTiles)

            val newEnergizedTiles = visitedTiles.keys.distinctBy { it.first }.size
            if (newEnergizedTiles > energizedTiles) energizedTiles = newEnergizedTiles
        }

        return energizedTiles
    }

    private fun Array<CharArray>.moveBeam(
        start: Coordinates,
        direction: Direction,
        visitedTiles: MutableMap<Pair<Coordinates, Direction>, Boolean>
    ) {
        val queue = mutableListOf(start to direction)

        while (queue.isNotEmpty()) {
            val (currentTile, currentDirection) = queue.removeAt(0)
            visitedTiles[currentTile to currentDirection] = true

            currentDirection.getNextDirections(this[currentTile.y][currentTile.x]).forEach {
                val nextTile = getNextTile(currentTile, it)
                if (nextTile != null && !visitedTiles.containsKey(nextTile to it)) {
                    queue.add(nextTile to it)
                }
            }
        }
    }

    private fun Array<CharArray>.getNextTile(currentPosition: Coordinates, direction: Direction): Coordinates? {
        val nextY = currentPosition.y + direction.coordinates.y
        val nextX = currentPosition.x + direction.coordinates.x

        if (nextY < 0 || nextY > size - 1) return null
        if (nextX < 0 || nextX > first().size - 1) return null

        return Coordinates(x = nextX, y = nextY)
    }

    private fun Direction.getNextDirections(tile: Char): Array<Direction> {
        if (tile == '.') return arrayOf(this)

        return when (this) {
            Direction.UP -> when (tile.toString()) {
                "|" -> arrayOf(this)
                "-" -> arrayOf(Direction.RIGHT, Direction.LEFT)
                "/" -> arrayOf(Direction.RIGHT)
                "\\" -> arrayOf(Direction.LEFT)
                else -> error("Wrong tile")
            }

            Direction.DOWN -> when (tile.toString()) {
                "|" -> arrayOf(this)
                "-" -> arrayOf(Direction.RIGHT, Direction.LEFT)
                "/" -> arrayOf(Direction.LEFT)
                "\\" -> arrayOf(Direction.RIGHT)
                else -> error("Wrong tile")
            }

            Direction.RIGHT -> when (tile.toString()) {
                "|" -> arrayOf(Direction.UP, Direction.DOWN)
                "-" -> arrayOf(this)
                "/" -> arrayOf(Direction.UP)
                "\\" -> arrayOf(Direction.DOWN)
                else -> error("Wrong tile")
            }

            Direction.LEFT -> when (tile.toString()) {
                "|" -> arrayOf(Direction.UP, Direction.DOWN)
                "-" -> arrayOf(this)
                "/" -> arrayOf(Direction.DOWN)
                "\\" -> arrayOf(Direction.UP)
                else -> error("Wrong tile")
            }
        }
    }

    private data class Coordinates(val x: Int, val y: Int)
}
