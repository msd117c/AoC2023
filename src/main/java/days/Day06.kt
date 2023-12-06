package days

import utils.InputReader.readInput

object Day06 {

    private const val TIME_PATTERN = "^Time:\\s+(.*)"
    private const val DISTANCE_PATTERN = "^Distance:\\s+(.*)"

    fun puzzle1() {
        readInput("day06Input") { lines ->
            val timeAndDistance = parseTimeAndDistanceInput(lines.toList())

            val result = timeAndDistance.map { entry ->
                getValidHoldingTimes(entry.key.toLong(), entry.value.toLong())
            }.reduce { acc, i -> acc.times(i) }

            println("Day 6 puzzle 1 result is: $result")
        }
    }

    private fun parseTimeAndDistanceInput(lines: List<String>): HashMap<Int, Int> {
        val timeAndDistance = hashMapOf<Int, Int>()
        val timeList = mutableListOf<Int>()
        val distanceList = mutableListOf<Int>()

        lines.forEach { line ->
            val timeGroup = Regex(TIME_PATTERN).find(line)?.groupValues?.last()
            timeGroup?.let { timeLine ->
                timeList.addAll(timeLine.replace(Regex("\\s+"), ",").split(",").map { it.toInt() })
            }

            val distanceGroup = Regex(DISTANCE_PATTERN).find(line)?.groupValues?.last()
            distanceGroup?.let { distanceLine ->
                distanceList.addAll(distanceLine.replace(Regex("\\s+"), ",").split(",").map { it.toInt() })
            }
        }

        timeList.forEachIndexed { index, time -> timeAndDistance[time] = distanceList[index] }

        return timeAndDistance
    }

    fun puzzle2() {
        readInput("day06Input") { lines ->
            val timeAndDistance = parseSingleRace(lines.toList())

            val result = getValidHoldingTimes(timeAndDistance.first, timeAndDistance.second)

            println("Day 6 puzzle 2 result is: $result")
        }
    }

    private fun parseSingleRace(lines: List<String>): Pair<Long, Long> {
        var time = 0L
        var distance = 0L

        lines.forEach { line ->
            val timeGroup = Regex(TIME_PATTERN).find(line)?.groupValues?.last()
            timeGroup?.let { timeLine ->
                time = timeLine.replace(Regex("\\s+"), "").toLong()
            }

            val distanceGroup = Regex(DISTANCE_PATTERN).find(line)?.groupValues?.last()
            distanceGroup?.let { distanceLine ->
                distance = distanceLine.replace(Regex("\\s+"), "").toLong()
            }
        }

        return time to distance
    }

    private fun getValidHoldingTimes(totalTime: Long, record: Long): Int {
        return (0 + 1 until totalTime).mapNotNull { distancePerMillisecond ->
            val distance = distancePerMillisecond.times(totalTime - distancePerMillisecond)

            if (distance > record) {
                distancePerMillisecond
            } else {
                null
            }
        }.size
    }
}
