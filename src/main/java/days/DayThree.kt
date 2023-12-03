package days

import utils.InputReader.readInput

object DayThree {

    private const val NUMBER_PATTERN = "(\\d+)"
    private const val SYMBOL_PATTERN = "(?!(\\d+))(?!(\\.))(.)"
    private const val GEAR_PATTERN = "(?!(\\d+))(?!(\\.))(\\*)"

    fun puzzle1() {
        readInput("dayThreeInput") { lines ->
            val input = lines.toList()

            val validParts = input.mapIndexed { index, line ->
                val previousLine = if (index - 1 >= 0) input[index - 1] else null
                val nextLine = if (index + 1 < input.size) input[index + 1] else null

                getParts(line, previousLine, nextLine)
            }.flatten()

            val total = validParts.sumOf { validPart -> validPart.number }
            println("Day 3 puzzle 1 result is: $total")
        }
    }

    private fun getParts(lineToCheck: String, previousLine: String?, nextLine: String?): List<Part> {
        val numberGroups = Regex(NUMBER_PATTERN).findAll(lineToCheck).toList().map { it.groups.first() }

        val parts = numberGroups.mapNotNull { group ->
            group?.let {
                val number = group.value.toInt()
                val position = (group.range.first - 1)..(group.range.last + 1)
                Part(number, position)
            }
        }

        val symbolGroupsPreviousLine = previousLine?.let {
            Regex(SYMBOL_PATTERN).findAll(previousLine).toList().map { it.groups.first() }
        }
        val symbolGroupsCurrentLine = Regex(SYMBOL_PATTERN).findAll(lineToCheck).toList().map { it.groups.first() }
        val symbolGroupsNextLine = nextLine?.let {
            Regex(SYMBOL_PATTERN).findAll(nextLine).toList().map { it.groups.first() }
        }

        val validParts = parts.filter { part ->
            symbolGroupsPreviousLine?.any { part.positionRange.contains(it?.range?.first) } ?: false ||
                    symbolGroupsCurrentLine.any { part.positionRange.contains(it?.range?.first) } ||
                    symbolGroupsNextLine?.any { part.positionRange.contains(it?.range?.first) } ?: false
        }

        return validParts
    }

    fun puzzle2() {
        readInput("dayThreeInput") { lines ->
            val input = lines.toList()

            val validGears = input.mapIndexed { index, line ->
                val previousLine = if (index - 1 >= 0) input[index - 1] else null
                val nextLine = if (index + 1 < input.size) input[index + 1] else null

                getGears(line, previousLine, nextLine)
            }.flatten()

            val total = validGears.sumOf { validPart -> validPart.ratio }
            println("Day 3 puzzle 2 result is: $total")
        }
    }

    private fun getGears(lineToCheck: String, previousLine: String?, nextLine: String?): List<Gear> {
        val gearGroups = Regex(GEAR_PATTERN).findAll(lineToCheck).toList().map { it.groups.first() }

        val gears = gearGroups.mapNotNull { group ->
            group?.let {
                val positionRange = (group.range.first - 1)..(group.range.first + 1)
                Gear(ratio = 0, positionRange)
            }
        }

        val numberGroupsPreviousLine = previousLine?.let {
            Regex(NUMBER_PATTERN).findAll(previousLine).toList().map { it.groups.first() }
        }
        val numberGroupsCurrentLine = Regex(NUMBER_PATTERN).findAll(lineToCheck).toList().map { it.groups.first() }
        val numberGroupsNextLine = nextLine?.let {
            Regex(NUMBER_PATTERN).findAll(nextLine).toList().map { it.groups.first() }
        }

        val validGears = gears.mapNotNull { gear ->
            val matches = numberGroupsPreviousLine?.filter { group ->
                group?.range?.any { position -> gear.positionRange.contains(position) } ?: false
            }.orEmpty() + numberGroupsCurrentLine.filter { group ->
                group?.range?.any { position -> gear.positionRange.contains(position) } ?: false
            } + numberGroupsNextLine?.filter { group ->
                group?.range?.any { position -> gear.positionRange.contains(position) } ?: false
            }.orEmpty()

            if (matches.size == 2) {
                val partValues = matches.mapNotNull { it?.value?.toInt() }
                val ratio = partValues.first() * partValues.last()

                gear.copy(ratio = ratio)
            } else {
                null
            }
        }

        return validGears
    }


    private data class Part(val number: Int, val positionRange: IntRange)
    private data class Gear(val ratio: Int, val positionRange: IntRange)
}
