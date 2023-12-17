package days

import utils.InputReader.readInput

object Day15 {

    fun puzzle1() {
        readInput("day15Input") { lines ->
            val initializationSequence = lines.toList().first().parseInitializationSequence()

            val hash = initializationSequence.sumOf { it.computeHash() }
            println("Day 15 puzzle 1 result is: $hash")
        }
    }

    fun puzzle2() {
        readInput("day15Input") { lines ->
            val initializationSequence = lines.toList().first().parseInitializationSequence()
            val boxes = mutableMapOf<Int, MutableList<Lens>>()

            initializationSequence.forEach { it.processInstruction(boxes) }
            val focusingPower = boxes.calculateFocusingPower()
            println("Day 15 puzzle 2 result is: $focusingPower")
        }
    }

    private fun String.processInstruction(boxes: MutableMap<Int, MutableList<Lens>>) {
        val operation = if (contains("=")) "=" else "-"
        val lensId = if (contains("=")) split("=").first() else split("-").first()
        val focalLength = if (operation == "=") split("=").last().toInt() else 0
        val boxId = lensId.computeHash()

        if (operation == "=") {
            val lens = Lens(lensId, focalLength)
            if (!boxes.containsKey(boxId)) {
                boxes[boxId] = mutableListOf(lens)
            } else {
                val lenses = boxes[boxId]!!
                val oldLensIndex = lenses.indexOfFirst { it.id == lensId }
                if (oldLensIndex == -1) {
                    boxes[boxId]?.add(lens)
                } else {
                    boxes[boxId]?.set(oldLensIndex, lens)
                }
            }
        } else {
            if (boxes.containsKey(boxId) && boxes[boxId]!!.any { it.id == lensId }) {
                val lenses = boxes[boxId]!!
                lenses.remove(lenses.first { it.id == lensId })
                boxes[boxId] = lenses
            }
        }
    }

    private fun MutableMap<Int, MutableList<Lens>>.calculateFocusingPower(): Int {
        return flatMap { keyAndValue ->
            val boxPosition = keyAndValue.component1() + 1
            keyAndValue.component2().mapIndexed { position, lens ->
                boxPosition * (position + 1) * lens.focalLength
            }
        }.sum()
    }

    private fun String.computeHash(): Int {
        var hash = 0

        forEach { c -> hash = ((hash + c.code).times(17)).mod(256) }

        return hash
    }

    private fun String.parseInitializationSequence(): List<String> = split(",")

    private data class Lens(val id: String, val focalLength: Int)
}
