package days

import utils.InputReader.readInput
import java.util.*

object Day12 {

    private const val PATTERN = "^(.*) (.*)"
    private const val BROKEN_PATTERN = "(\\.+)"
    private const val LEADING_BROKEN_PATTERN = "^(\\.+)"
    private const val TRAILING_BROKEN_PATTERN = "(\\.+)\$"

    fun puzzle1() {
        readInput("day12Input") { lines ->
            val records = lines.toList().map(::parseRecord)

            val firstGapRecords = records.map(::step3FillFirstGaps)
            val lastGapRecords = firstGapRecords.map(::step3FillLastGaps)

            // OK
            val directRecords = lastGapRecords.filter(::step0CheckAgainstIdealRecord)

            val nonDirectRecords = lastGapRecords.filterNot(::step0CheckAgainstIdealRecord)
            val collapsedRecords = nonDirectRecords.map(::step1CollapseRecord)

            val cleanedRecords = collapsedRecords.map(::step2ReplaceObviousValues).filterNot { it.value.contains("?") }

            val nonCleanedRecords = collapsedRecords.map(::step2ReplaceObviousValues).filter { it.value.contains("?") }
            val possibleCombinations = nonCleanedRecords.map(::numberOfCombinations).sum()

            val result = directRecords.size + cleanedRecords.size + possibleCombinations
            println("Day 12 puzzle 1 result is: $result")
        }
    }

    private fun parseRecord(line: String): Record {
        val groups = Regex(PATTERN).find(line)?.groupValues ?: throw IllegalArgumentException("Record is invalid!")
        val value = groups[1]
        val ranges = groups[2].split(",").map { it.toInt() }

        return Record(value, ranges)
    }

    private fun step0CheckAgainstIdealRecord(record: Record): Boolean {
        var idealRecordValue = ""
        record.ranges.forEachIndexed { index, amount ->
            idealRecordValue += "#".repeat(amount) + ".".takeIf { index < record.ranges.size - 1 }.orEmpty()
        }

        val valueToCompare = record.value
            .replace(Regex(LEADING_BROKEN_PATTERN), "")
            .replace(Regex(TRAILING_BROKEN_PATTERN), "")

        return idealRecordValue.length == valueToCompare.length
    }

    private fun step1CollapseRecord(record: Record): Record {
        val newValue = record.value.replace(Regex(BROKEN_PATTERN), ".")

        return record.copy(value = newValue)
    }

    private fun step2ReplaceObviousValues(record: Record): Record {
        val newValues = record.value.split(".").filter { it.isNotEmpty() }

        // Fix this
        return if (newValues.size > record.ranges.size) {
            record
        } else {
            val newValue = newValues.mapIndexed { index, value ->
                if (value.length == record.ranges[index]) {
                    value.replace("?", "#")
                } else {
                    value
                }
            }.joinToString(".")

            record.copy(value = newValue)
        }
    }

    private fun step3FillFirstGaps(record: Record): Record {
        var firstHalf = record.value.substring(0, record.ranges.first())
        val secondHalf = record.value.substring(record.ranges.first(), record.value.length)

        return if (firstHalf.contains("#")) {
            val firstIndex = firstHalf.indexOf('#')
            val lastIndex = firstHalf.lastIndexOf('#')
            firstHalf = firstHalf.substring(0, firstIndex) + "#".repeat(lastIndex - firstIndex) + firstHalf.substring(
                lastIndex,
                firstHalf.length
            )

            record.copy(value = firstHalf + secondHalf)
        } else {
            record
        }
    }

    private fun step3FillLastGaps(record: Record): Record {
        val groupLength = record.ranges.last()
        val firstHalf = record.value.substring(0, record.value.length - groupLength)
        var secondHalf = record.value.substring(record.value.length - groupLength, record.value.length)

        return if (secondHalf.contains("#")) {
            val firstIndex = secondHalf.indexOf('#')
            val lastIndex = secondHalf.lastIndexOf('#')
            secondHalf =
                secondHalf.substring(0, firstIndex) + "#".repeat(lastIndex - firstIndex) + secondHalf.substring(
                    lastIndex,
                    secondHalf.length
                )

            record.copy(value = firstHalf + secondHalf)
        } else {
            record
        }
    }

    /*
     .??????#???#??????? 2,7,1,1
     ??????##??#???????? 1,10,1
     */
    private fun numberOfCombinations(record: Record): Int {
        val unknownIndices = record.value.indices.filter { record.value[it] == '?' }
        val missingParts = record.ranges.sum() - record.value.count { it == '#' }
        val missingDots = unknownIndices.size - missingParts

        val partsInput = (0 until missingParts).map { '#' }
        val dotsInput = (0 until missingDots).map { '.' }
        val input = partsInput + dotsInput

        var number = 0
        val combinations = allPermutations(input)
        println("${record.value} - ${input.size} - ${combinations.size}")

        while (combinations.isNotEmpty()) {
            val combination = combinations.first()
            combinations.remove(combination)

            val newValue = record.value.mapIndexed { index, c ->
                if (unknownIndices.contains(index)) {
                    combination[unknownIndices.indexOf(index)]
                } else {
                    c
                }
            }.joinToString("")

            if (isValidCombination(record.copy(value = newValue))) number++
        }

        return number
    }

    private fun <T> allPermutations(set: List<T>): MutableSet<List<T>> {
        if (set.isEmpty()) return mutableSetOf()

        fun <T> _allPermutations(list: List<T>): MutableSet<List<T>> {
            if (list.isEmpty()) return mutableSetOf(emptyList())

            val result: MutableSet<List<T>> = mutableSetOf()
            for (i in list.indices) {
                _allPermutations(list - list[i]).forEach {
                        item -> result.add(item + list[i])
                }
            }
            return result
        }

        return _allPermutations(set.toList())
    }

    private fun isValidCombination(record: Record): Boolean {
        val groups = record.value.split(".").filter { it.isNotEmpty() }
        val amounts = record.ranges

        return groups.mapIndexed { index, value ->
            index < amounts.size && value.length == amounts[index]
        }.all { it }
    }

    private data class Record(val value: String, val ranges: List<Int>)
}
