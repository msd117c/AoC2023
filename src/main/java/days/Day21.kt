package days

import utils.InputReader.readInput

object Day21 {

    @JvmStatic
    fun main(args: Array<String>) {
        puzzle1()
        puzzle2()
    }

    private fun puzzle1() {
        readInput("day21Input") { lines ->
            val map = lines.toList().map { it.toCharArray() }

            val distances = map.dijkstra(steps = 64)
            println("Day 21 puzzle 1 result is: ${distances[64]!!.size}")
        }
    }

    private fun puzzle2() {
        readInput("day21Input") { lines ->
            val map = lines.toList().map { it.toCharArray() }

            val distances = map.dijkstra2(steps = 17)
            val evenCorners = distances.values.filter { it.mod(2) == 0 && it > 16 }.size
            val oddCorners = distances.values.filter { it.mod(2) == 1 && it > 16 }.size

            val evenFull = distances.values.filter { it.mod(2) == 0 }.size
            val oddFull = distances.values.filter { it.mod(2) == 1 }.size

            val n = 151 // 202300L
            val result = ((n+1)*(n*1)) * oddFull + (n*n) * evenFull - (n+1) * oddCorners + n * evenCorners
            println("Day 21 puzzle 2 result is: $result")
        }
    }

    private fun List<CharArray>.dijkstra(steps: Int): Map<Int, List<Pair<Int, Int>>> {
        var step = 0
        val y = indexOfFirst { it.contains('S') }
        val x = this[y].indexOfFirst { c -> c == 'S' }

        val origin = x to y
        val queue = mutableListOf(listOf(origin))
        val distance = mutableMapOf(0 to listOf(origin))

        while (queue.isNotEmpty()) {
            val currentPositions = queue.removeFirst()

            if (step == steps) return distance

            step++

            val nextPositions = currentPositions.map { current -> findNeighbors(current) }.flatten().distinct()
            distance[step] = nextPositions
            queue.add(nextPositions)
        }

        error("Something went wrong")
    }

    private fun List<CharArray>.dijkstra2(steps: Int): Map<Pair<Int, Int>, Int> {
        var step = 0
        val y = indexOfFirst { it.contains('S') }
        val x = this[y].indexOfFirst { c -> c == 'S' }

        val origin = x to y
        val queue = mutableListOf(listOf(origin))
        val distance = mutableMapOf(origin to 0)

        while (queue.isNotEmpty()) {
            val currentPositions = queue.removeFirst()

            if (step == steps) return distance

            step++

            val nextPositions = currentPositions.map { current -> findNeighbors(current) }.flatten().distinct()
            nextPositions.forEach {
                distance[it] = step
            }
            queue.add(nextPositions)
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
