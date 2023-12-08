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

            val steps = navigate(instructions, nodes, startId, endId)
            println("Day 8 puzzle 1 result is: $steps")
        }
    }

    private fun navigate(instructions: CharArray, nodes: List<Node>, startId: String, endId: String): Int {
        val queue = arrayListOf(nodes.first { it.id == startId })

        while (queue.all { it.id != endId }) {
            instructions.forEach { instruction ->
                val nextNode = when (instruction) {
                    'R' -> queue.last().right
                    'L' -> queue.last().left
                    else -> throw IllegalArgumentException("Not valid instruction!")
                }
                queue.add(nodes.first { it.id == nextNode })
                if (queue.any { it.id == endId }) return@forEach
            }
        }

        return queue.size - 1
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
