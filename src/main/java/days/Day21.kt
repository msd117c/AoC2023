package days

import utils.InputReader.readInput

object Day21 {

    @JvmStatic
    fun main(args: Array<String>) {
        puzzle1()
    }

    private fun puzzle1() {
        readInput("day21Input") { lines ->
            val map = lines.toList().map { it.toCharArray() }

            val plots = map.dijkstra(steps = 64)
            println("Day 21 puzzle 1 result is: $plots")
        }
    }

    private fun List<CharArray>.dijkstra(steps: Int): Int {
        var step = 0
        val y = indexOfFirst { it.contains('S') }
        val x = this[y].indexOfFirst { c -> c == 'S' }

        val origin = x to y
        val queue = mutableListOf(listOf(origin))
        var plots = 0

        while (queue.isNotEmpty()) {
            val currentPositions = queue.removeFirst()

            if (step == steps) return plots

            val nextPositions = currentPositions.map { current -> findNeighbors(current) }.flatten().distinct()
            queue.add(nextPositions)

            plots = queue.sumOf { it.size }

            step++
        }

        error("Something went wrong")
    }

    private fun List<CharArray>.findNeighbors(origin: Pair<Int, Int>): List<Pair<Int, Int>> {
        val x = origin.first
        val y = origin.second

        return listOf(
            x + 1 to y,
            x - 1 to y,
            x to y + 1,
            x to y - 1,
        ).filter { (x, y) ->
            x in 0..(first().lastIndex) && y in 0..(lastIndex)
        }.filter { (x, y) ->
            this[y][x] != '#'
        }
    }
}
