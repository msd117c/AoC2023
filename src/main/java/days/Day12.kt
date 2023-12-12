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

            // OK
            val directRecords = records.filter(::step0CheckAgainstIdealRecord)

            val nonDirectRecords = records.filterNot(::step0CheckAgainstIdealRecord)
            val collapsedRecords = nonDirectRecords.map(::step1CollapseRecord)
            collapsedRecords.forEach {
                println("${it.value} ${it.ranges.joinToString(",")}")
            }
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

    private fun numberOfCombinations(record: Record): Int {
        val unknownIndices = record.value.indices.filter { record.value[it] == '?' }
        val missingParts = record.ranges.sum() - record.value.count { it == '#' }
        val missingDots = unknownIndices.size - missingParts

        val partsInput = (0 until missingParts).map { '#' }
        val dotsInput = (0 until missingDots).map { '.' }
        val input = partsInput + dotsInput

        val possibleCombinations = permutations(input).map { it.joinToString("") }.distinct()

        val combinations = possibleCombinations.mapNotNull { combination ->
            val newValue = record.value.mapIndexed { index, c ->
                if (unknownIndices.contains(index)) {
                    combination[unknownIndices.indexOf(index)]
                } else {
                    c
                }
            }.joinToString("")

            val newRecord = record.copy(value = newValue)
            val isValid = isValidCombination(record.copy(value = newValue))

            if (isValid) {
                newRecord
            } else {
                null
            }
        }.distinct()

        return combinations.size
    }

    private fun permutations(input: List<Char>): List<CharArray> {
        val solutions = mutableListOf<CharArray>()
        permutationsRecursive(input, 0, solutions)
        return solutions
    }


    private fun permutationsRecursive(input: List<Char>, index: Int, answers: MutableList<CharArray>) {
        if (index == input.lastIndex) answers.add(input.toCharArray())
        for (i in index..input.lastIndex) {
            Collections.swap(input, index, i)
            permutationsRecursive(input, index + 1, answers)
            Collections.swap(input, i, index)
        }
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
