package days

import utils.InputReader.readInput

object Day08 {

    private const val NODE_PATTERN = "^(.*) = \\((.*), (.*)\\)"

    fun puzzle1() {
        readInput("day08Input") { lines ->
            val startId = "AAA"
            val endId = "ZZZ"
            val input = lines.toList()

            val instructions = input.first().toCharArray()
            val nodes = input.drop(1).subList(
                input.indexOfFirst { line -> line.isEmpty() },
                input.size - 1
            ).map(::parseNode)
            val start = nodes.first { it.id == startId }
            val end = nodes.first { it.id == endId }

            val distances = calculateMinDistance(nodes, start, listOf(end), instructions)
            println("Day 8 puzzle 1 result is: $distances")
        }
    }

    fun puzzle2() {
        readInput("day08Input") { lines ->
            val input = lines.toList()

            val instructions = input.first().toCharArray()
            val nodes = input.drop(1).subList(
                input.indexOfFirst { line -> line.isEmpty() },
                input.size - 1
            ).map(::parseNode)
            val startNodes = nodes.filter { node -> node.id.endsWith("A") }
            val endNodes = nodes.filter { node -> node.id.endsWith("Z") }

            val distances = startNodes.map { startNode ->
                calculateMinDistance(nodes, startNode, endNodes, instructions)
            }
            val min = distances.reduce { total, next -> lcm(total, next) }
            println("Day 8 puzzle 2 result is: $min")
        }
    }

    private fun lcm(a: Long, b: Long) = a / gcd(a, b) * b

    private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

    private fun calculateMinDistance(
        graph: List<Node>,
        start: Node,
        endNodes: List<Node>,
        instructions: CharArray
    ): Long {
        var finished = false
        val queue = mutableListOf(start)

        var steps = 0L
        while (!endNodes.contains(queue.last()) || !finished) {
            instructions.forEach { instruction ->
                val current = queue.removeAt(0)

                val neighbor = when (instruction) {
                    'R' -> graph.first { it.id == current.right }
                    'L' -> graph.first { it.id == current.left }
                    else -> throw IllegalArgumentException("Bad instruction")
                }

                queue.add(neighbor)

                steps++

                if (endNodes.contains(queue.last())) {
                    finished = true
                    return@forEach
                }
            }
        }

        return steps
    }

    private fun parseNode(line: String): Node {
        val groups = Regex(NODE_PATTERN).find(line)?.groupValues ?: throw IllegalArgumentException("Not valid input!")

        val id = groups[1]
        val left = groups[2]
        val right = groups[3]

        return Node(id, left, right)
    }

    private data class Node(val id: String, val left: String, val right: String)
}
