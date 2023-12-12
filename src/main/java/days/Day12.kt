package days

import utils.InputReader.readInput
import java.lang.IllegalArgumentException

object Day12 {

    private const val PATTERN = "^(.*) (.*)"
    private const val VALID_PARTS_PATTERN = "(#+)"
    private const val UNKNOWN_PATTERN = "(\\?+)"

    fun puzzle1() {
        readInput("day12Input") { lines ->
            val records = lines.toList().map(::parseRecord)
            val brokenParts = setBrokenParts(records[5])
        }
    }

    private fun parseRecord(line: String): Record {
        val groups = Regex(PATTERN).find(line)?.groupValues ?: throw IllegalArgumentException("Record is invalid!")
        val value = groups[1]
        val ranges = groups[2].split(",").map { it.toInt() }

        return Record(value, ranges)
    }

    private fun getNumberOfCombinations(record: Record): Int {
        // ???.### 1,1,3 -> 1
        // #.#.### 1,1,3 -> 1
        // check missing ranges
        val unknownPositions = record.value.indices.filter { record.value[it] == '?' }
        val knownPositions = record.value.indices.filter { record.value[it] == '#' }
        val brokenPositionsMissing =
            (record.value.length - record.ranges.sum()) - record.value.indices.filter { record.value[it] == '.' }.size
        val nonBrokenPositionsMissing = record.ranges.sum() - knownPositions.size

        return 0
    }

    /*
        // GET THIS!!
        ?###???????? 3,2,1
        .###.???????

        ?#?#?#?#?#?#?#? 1,3,1,6
        .#.#?#.#.#?#?#?


        ???.###
        ??
         ??
          ?.###
     */
    private fun setBrokenParts(record: Record): Record {
        val ranges = mutableListOf<IntRange>()
        record.ranges.forEachIndexed { index, amount ->
            val start = if (index == 0) {
                0
            } else {
                ranges[index - 1].last + 1
            }

            val end = if (index == record.ranges.size - 1) {
                record.value.length
            } else {
                start + amount + 1
            }

            ranges.add(start until end)
        }

        val valuePairs = ranges.mapIndexed { index, range ->
            val value = record.value.substring(range)
            val amount = record.ranges[index]

            val fixedValue = when (amount) {
                value.count { it == '#' } -> value.replace('?', '.')
                value.count { it == '?' } -> value.replace('?', '#')
                else -> value
            }

            fixedValue to amount
        }
        val fixedValue = valuePairs.mapIndexed { index, (value, amount) ->
            val previousSymbol = if (index == 0) {
                null
            } else {
                valuePairs[index - 1].first.last()
            }

            val firstSymbol = value.first()

            val fixedFirstSymbol = if (firstSymbol == '?') {
                if (previousSymbol == '#') {
                    '.'
                } else {
                    firstSymbol
                }
            } else {
                firstSymbol
            }

            value.replaceFirst(firstSymbol, fixedFirstSymbol) to amount
        }

        val amountsToLocate = fixedValue.map { (value, amount) ->
            if (value.contains('?')) {
                amount
            } else {
                null
            }
        }

        return record
    }

    private data class Record(val value: String, val ranges: List<Int>)
    private data class RecordSegmentGroup(
        val previous: Pair<String, Int>?,
        val current: Pair<String, Int>,
        val next: Pair<String, Int>?
    )
}
