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

            val distances = map.dijkstra()
            println("Day 21 puzzle 1 result is: ${distances.values.count { it <= 64 && it.mod(2) == 0 }}")
        }
    }

    private fun puzzle2() {
        readInput("day21Input") { lines ->
            val map = lines.toList().map { it.toCharArray() }
            val steps = 26501365L
            val distanceToEdge = map.size.div(2)

            val distances = map.dijkstra()

            val evenCorners = distances.values.count { it.mod(2) == 0 && it > distanceToEdge }
            val oddCorners = distances.values.count { it.mod(2) == 1 && it > distanceToEdge }

            val evenFull = distances.values.count { it.mod(2) == 0 }
            val oddFull = distances.values.count { it.mod(2) == 1 }

            val n = (steps - distanceToEdge).div(map.size)
            val result =
                (((n + 1) * (n + 1)) * oddFull) + ((n * n) * evenFull) - ((n + 1) * oddCorners) + (n * evenCorners)
            println("Day 21 puzzle 2 result is: $result")
        }
    }

    private fun List<CharArray>.dijkstra(): Map<Pair<Int, Int>, Int> {
        val y = indexOfFirst { it.contains('S') }
        val x = this[y].indexOfFirst { c -> c == 'S' }

        val origin = x to y
        val queue = mutableListOf(origin)
        val distance = mutableMapOf(origin to 0)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            findNeighbors(current).forEach { neighbor ->
                val newDistance = distance[current]!! + 1

                if (!distance.containsKey(neighbor)) {
                    distance[neighbor] = newDistance
                    queue.add(neighbor)
                }
            }
        }

        return distance
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
