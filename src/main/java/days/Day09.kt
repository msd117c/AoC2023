package days

import utils.InputReader.readInput
import java.lang.IllegalArgumentException

object Day09 {

    fun puzzle1() {
        readInput("day09Input") { lines ->
            val histories = lines.toList().map(::parseHistory)
            val sequences = histories.map(::getSequence)

            val nextValues = histories.mapIndexed { index, history -> predictNextValue(history, sequences[index]) }
            println("Day 9 puzzle 1 result is: ${nextValues.sum()}")
        }
    }

    private fun predictNextValue(history: List<Int>, sequence: List<Any>): Int {
        val lastValue = history.last()
        val increment = getIncrement(sequence)

        return lastValue + increment
    }

    private fun getIncrement(sequence: List<Any>): Int {
        return when {
            (sequence as? List<Int>) != null -> {
                if ((sequence.first() as? List<Int>) != null) {
                    (sequence.first() as List<Int>).last() + getIncrement(sequence.last() as List<Any>)
                } else {
                    sequence.first()
                }
            }
            else -> throw IllegalArgumentException("Not valid")
        }
    }

    fun puzzle2() {
        readInput("day09Input") { lines ->
            val histories = lines.toList().map(::parseHistory)
            val sequences = histories.map(::getSequence)

            val previousValues = histories.mapIndexed { index, history -> predictPreviousValue(history, sequences[index]) }
            println("Day 9 puzzle 2 result is: ${previousValues.sum()}")
        }
    }

    private fun predictPreviousValue(history: List<Int>, sequence: List<Any>): Int {
        val lastValue = history.first()
        val increment = getDecrement(sequence)

        return lastValue - increment
    }

    private fun getDecrement(sequence: List<Any>): Int {
        return when {
            (sequence as? List<Int>) != null -> {
                if ((sequence.first() as? List<Int>) != null) {
                    (sequence.first() as List<Int>).first() - getDecrement(sequence.last() as List<Any>)
                } else {
                    sequence.first()
                }
            }
            else -> throw IllegalArgumentException("Not valid")
        }
    }

    private fun parseHistory(line: String): List<Int> {
        return line.split(" ").map { it.toInt() }
    }

    private fun getSequence(history: List<Int>): List<Any> {
        val sequence = history.windowed(size = 2, step = 1).map { values ->
            values.last() - values.first()
        }

        return if (sequence.distinct().size == 1) {
            sequence
        } else {
            listOf(sequence, getSequence(sequence))
        }
    }
}
