package days

import utils.InputReader.readInput

object DayOne {

    // Puzzle 1-1
    fun puzzle1() {
        readInput("dayOneInput") { lines ->
            val result = lines.map(::getLineDigitsPuzzle1).sum()
            println("Day 1 puzzle 1 result is: $result")
        }
    }

    private fun getLineDigitsPuzzle1(line: String): Int {
        val firstDigit = line.firstOrNull { it.isDigit() }.getDigitOrThrow()
        val lastDigit = line.lastOrNull { it.isDigit() }.getDigitOrThrow()

        return "$firstDigit$lastDigit".toInt()
    }

    private fun Char?.getDigitOrThrow(): String {
        return this?.toString() ?: throw IllegalArgumentException("Must contain at least one digit!")
    }

    // Puzzle 1-2
    fun puzzle2() {
        readInput("dayOneInput") { lines ->
            val result = lines.map(::getLineDigitsPuzzle2).sum()
            println("Day 1 puzzle 2 result is: $result")
        }
    }

    private val validDigits = hashMapOf(
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9",
    )

    private fun getLineDigitsPuzzle2(line: String): Int {
        val normalizedLine = line.lowercase()

        val firstDigit = detectFirstDigit(normalizedLine)
        val lastDigit = detectLastDigit(normalizedLine)

        return "$firstDigit$lastDigit".toInt()
    }

    private fun detectFirstDigit(line: String): String {
        val candidates = (
                validDigits.mapNotNull { (key, value) ->
                    val candidate = Candidate(
                        position = line.replaceFirst(key, value).indexOfFirst { c -> c.toString() == value },
                        digit = value
                    )

                    candidate.takeUnless { candidate.position == -1 }
                } + listOf(
                    Candidate(
                        position = line.indexOfFirst { it.isDigit() },
                        digit = line.firstOrNull { it.isDigit() }.toString()
                    )
                )
                ).sortedBy { candidate -> candidate.position }

        return candidates.firstOrNull()?.digit ?: throw IllegalArgumentException("No candidate!")
    }

    private fun detectLastDigit(line: String): String {
        val candidates = (
                validDigits.mapNotNull { (key, value) ->
                    val candidate = Candidate(
                        position = line.replaceAfterLast(key, value).indexOfLast { c -> c.toString() == value },
                        digit = value,
                    )

                    candidate.takeUnless { candidate.position == -1 }
                } + listOf(
                    Candidate(
                        position = line.indexOfLast { it.isDigit() },
                        digit = line.lastOrNull { it.isDigit() }.toString()
                    )
                )
                ).sortedBy { candidate -> candidate.position }

        return candidates.lastOrNull()?.digit ?: throw IllegalArgumentException("No candidate!")
    }

    private data class Candidate(val position: Int, val digit: String)
}
