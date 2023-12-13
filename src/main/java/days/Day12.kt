package days

import utils.InputReader.readInput

object Day12 {

    private const val PATTERN = "^(.*) (.*)"

    fun puzzle1() {
        readInput("day12Input") { lines ->
            val result = lines.map { line ->
                val record = parseRecord(line)
                countArrangements(record)
            }.sum()

            println("Day 12 puzzle 1 result is: $result")
        }
    }

    private fun countArrangements(record: Record): Int {
        val partsAmount = record.ranges.sum()
        val unassignedParts = partsAmount - record.value.count { it == '#' }
        val unknownPositions = record.value.indices.filter { record.value[it] == '?' }

        var combinations = 0
        val options = unknownPositions.combinations(unassignedParts)

        options.forEach { option ->
            val newValue = record.value.toCharArray()
            option.forEach { index ->
                newValue[index] = '#'
            }
            val recordValue = newValue.joinToString("").replace('?', '.')
            if (isValidCombination(record.copy(value = recordValue))) combinations++
        }

        return combinations
    }

    /**
     * Return r length [List]s of T from this List which are emitted in lexicographic sort order.
     * So, if the input iterable is sorted, the combination tuples will be produced in sorted order.
     * Elements are treated as unique based on their position, not on their value.
     * So if the input elements are unique, there will be no repeat values in each combination.
     *
     * @param r How many elements to pick
     * @param replace elements are replaced after being chosen
     *
     * @return [Sequence] of all possible combinations of length r
     */
    private fun <T : Any> List<T>.combinations(r: Int, replace: Boolean = false): Sequence<List<T>> {
        val n = count()
        if (r > n) return sequenceOf()
        return sequence {
            var indices = if (replace) 0.repeat(r).toMutableList() else (0 until r).toMutableList()
            while (true) {
                yield(indices.map { this@combinations[it] })
                var i = r - 1
                loop@ while (i >= 0) {
                    when (replace) {
                        true -> if (indices[i] != n - 1) break@loop
                        false -> if (indices[i] != i + n - r) break@loop
                    }
                    i--
                }
                if (i < 0) break
                when (replace) {
                    true -> indices = (indices.take(i) + (indices[i] + 1).repeat(r - i)).toMutableList()
                    false -> {
                        indices[i] += 1
                        (i + 1 until r).forEach { indices[it] = indices[it - 1] + 1 }
                    }
                }
            }
        }
    }

    private fun <T : Any> T.repeat(times: Int? = null): Sequence<T> = sequence {
        var count = 0
        while (times == null || count++ < times) yield(this@repeat)
    }

    fun puzzle2() {
        readInput("day12Input") { lines ->
            val result = lines.map { line ->
                val record = parseRecord(line)
                val unfoldedRecord = unfold(record)
                0
            }.sum()

            println("Day 12 puzzle 2 result is: $result")
        }
    }

    private fun unfold(record: Record): Record {
        return record.copy(
            value = record.value.repeat(5).joinToString(""),
            ranges = record.ranges.repeat(5).flatten().toList()
        )
    }

    private fun parseRecord(line: String): Record {
        val groups = Regex(PATTERN).find(line)?.groupValues ?: throw IllegalArgumentException("Record is invalid!")
        val value = groups[1]
        val ranges = groups[2].split(",").map { it.toInt() }

        return Record(value, ranges)
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
