package days

import days.Day05.MapType.*
import utils.InputReader.readInput

object Day05 {

    private const val SEEDS_PATTERN = "^seeds: (.*)"
    private const val SEED_TO_SOIL_PATTERN = "seed-to-soil map:"
    private const val SOIL_TO_FERTILIZER_PATTERN = "soil-to-fertilizer map:"
    private const val FERTILIZER_TO_WATER_PATTERN = "fertilizer-to-water map:"
    private const val WATER_TO_LIGHT_PATTERN = "water-to-light map:"
    private const val LIGHT_TO_TEMPERATURE_PATTERN = "light-to-temperature map:"
    private const val TEMPERATURE_TO_HUMIDITY_PATTERN = "temperature-to-humidity map:"
    private const val HUMIDITY_TO_LOCATION_PATTERN = "humidity-to-location map:"

    private enum class MapType(val mapName: String) {
        SEED_TO_SOIL("seed-to-soil"),
        SOIL_TO_FERTILIZER("soil-to-fertilizer"),
        FERTILIZER_TO_WATER("fertilizer-to-water"),
        WATER_TO_LIGHT("water-to-light"),
        LIGHT_TO_TEMPERATURE("light-to-temperature"),
        TEMPERATURE_TO_HUMIDITY("temperature_to_humidity"),
        HUMIDITY_TO_LOCATION("humidity-to-location"),
    }

    fun puzzle1() {
        readInput("day05Input") { lines ->
            var seedsLine = ""
            val groups = hashMapOf<MapType, List<String>>()
            var currentGroup: MapType? = null

            lines.toList().forEach { line ->
                if (seedsLine.isEmpty()) {
                    seedsLine = Regex(SEEDS_PATTERN).find(line)?.groupValues?.get(1)
                        ?: throw IllegalArgumentException("Seeds not found")
                }
                if (currentGroup == null) {
                    when (line) {
                        SEED_TO_SOIL_PATTERN -> currentGroup = SEED_TO_SOIL
                        SOIL_TO_FERTILIZER_PATTERN -> currentGroup = SOIL_TO_FERTILIZER
                        FERTILIZER_TO_WATER_PATTERN -> currentGroup = FERTILIZER_TO_WATER
                        WATER_TO_LIGHT_PATTERN -> currentGroup = WATER_TO_LIGHT
                        LIGHT_TO_TEMPERATURE_PATTERN -> currentGroup = LIGHT_TO_TEMPERATURE
                        TEMPERATURE_TO_HUMIDITY_PATTERN -> currentGroup = TEMPERATURE_TO_HUMIDITY
                        HUMIDITY_TO_LOCATION_PATTERN -> currentGroup = HUMIDITY_TO_LOCATION
                    }
                } else {
                    if (line.isEmpty()) {
                        currentGroup = null
                    } else {
                        groups[currentGroup!!] = groups[currentGroup].orEmpty() + listOf(line)
                    }
                }
            }

            val seeds = seedsLine.split(" ").map { it.toLong() }
            val maps = hashMapOf<MapType, List<MapRange>>()
            groups.forEach { parseMap(it, maps) }

            val locations = seeds.map { seed -> getLocation(maps, seed) }

            println("Day 5 puzzle 1 result is: ${locations.min()}")
        }
    }

    private fun getLocation(maps: HashMap<MapType, List<MapRange>>, seed: Long): Long {
        val soil = getMappedValue(maps, SEED_TO_SOIL, seed)
        val fertilizer = getMappedValue(maps, SOIL_TO_FERTILIZER, soil)
        val water = getMappedValue(maps, FERTILIZER_TO_WATER, fertilizer)
        val light = getMappedValue(maps, WATER_TO_LIGHT, water)
        val temperature = getMappedValue(maps, LIGHT_TO_TEMPERATURE, light)
        val humidity = getMappedValue(maps, TEMPERATURE_TO_HUMIDITY, temperature)

        return getMappedValue(maps, HUMIDITY_TO_LOCATION, humidity)
    }

    private fun getMappedValue(maps: HashMap<MapType, List<MapRange>>, mapType: MapType, input: Long): Long {
        maps[mapType]?.forEach { mapRange ->
            if ((mapRange.source until mapRange.source + mapRange.length).contains(input)) {
                val index = input - mapRange.source
                return mapRange.destination + index
            }
        }

        return input
    }

    private fun parseMap(group: Map.Entry<MapType, List<String>>, maps: HashMap<MapType, List<MapRange>>) {
        group.value.forEach { line ->
            val values = line.split(" ")
            val destinationStart = values[0].toLong()
            val sourceStart = values[1].toLong()
            val length = values[2].toLong()

            val mapRange = MapRange(sourceStart, destinationStart, length)
            maps[group.key] = maps[group.key].orEmpty() + mapRange
        }
    }

    fun puzzle2() {
        val groups = hashMapOf<Int, List<String>>()
        var seedsLine = ""

        readInput("day05Input") { lines ->
            var currentGroup = -1

            lines.toList().forEach { line ->
                if (seedsLine.isEmpty()) {
                    seedsLine = Regex(SEEDS_PATTERN).find(line)?.groupValues?.get(1)
                        ?: throw IllegalArgumentException("Seeds not found")
                }
                if (currentGroup == -1) {
                    when (line) {
                        SEED_TO_SOIL_PATTERN -> currentGroup = 0
                        SOIL_TO_FERTILIZER_PATTERN -> currentGroup = 1
                        FERTILIZER_TO_WATER_PATTERN -> currentGroup = 2
                        WATER_TO_LIGHT_PATTERN -> currentGroup = 3
                        LIGHT_TO_TEMPERATURE_PATTERN -> currentGroup = 4
                        TEMPERATURE_TO_HUMIDITY_PATTERN -> currentGroup = 5
                        HUMIDITY_TO_LOCATION_PATTERN -> currentGroup = 6
                    }
                } else {
                    if (line.isEmpty()) {
                        currentGroup = -1
                    } else {
                        groups[currentGroup] = groups[currentGroup].orEmpty() + listOf(line)
                    }
                }
            }
        }

        val maps = hashMapOf<Int, List<MapRange2>>()
        groups.forEach { parseMap2(it, maps) }

        val seedGroups = seedsLine.split(" ").map { it.toLong() }.windowed(size = 2, step = 2)

        val minLocation = seedGroups.minOfOrNull { data ->
            val ranges = listOf(data.first() until data.first() + data.last())
            transformRange(ranges, 0, maps)
        } ?: 0L

        println("Day 5 puzzle 2 result is: $minLocation")
    }

    private fun transformRange(
        ranges: List<LongRange>,
        mapIndex: Int,
        maps: HashMap<Int, List<MapRange2>>
    ): Long {
        if (mapIndex >= maps.size) return ranges.minOfOrNull { it.first } ?: 0L

        val transformedRanges = mutableListOf<LongRange>()

        ranges.forEach { range ->
            val intersections = maps[mapIndex]!!.mapNotNull { mapRange ->

                val first = if (mapRange.sourceRange.first - range.first >= 0) {
                    mapRange.sourceRange.first
                } else {
                    range.first
                }

                val last = if (mapRange.sourceRange.last - range.last >= 0) {
                    range.last
                } else {
                    mapRange.sourceRange.last
                }

                if (last > first) {
                    MapRange2(LongRange(first, last), mapRange.transformation)
                } else {
                    null
                }
            }

            var remainingRanges = listOf(range)

            intersections.forEach { intersection ->
                val cutRanges = mutableListOf<LongRange>()
                remainingRanges.forEach { rr ->
                    var first = if (rr.first - intersection.sourceRange.first < 0) {
                        rr.first
                    } else {
                        null
                    }
                    var last = if (rr.last - intersection.sourceRange.first >= 0) {
                        intersection.sourceRange.first - 1
                    } else {
                        null
                    }

                    if (last != null && first != null && last > first) {
                        cutRanges.add(LongRange(first, last))
                    }

                    if (rr.last - intersection.sourceRange.last > 0) {
                        first = intersection.sourceRange.last + 1
                        last = rr.last

                        if (last > first) {
                            cutRanges.add(LongRange(first, last))
                        }
                    }
                }
                remainingRanges = cutRanges
            }
            transformedRanges.addAll(remainingRanges)

            val listToAdd = intersections.map { intersection ->
                LongRange(
                    intersection.sourceRange.first() + intersection.transformation,
                    intersection.sourceRange.last() + intersection.transformation
                )
            }

            transformedRanges.addAll(listToAdd)
        }

        return transformRange(transformedRanges, mapIndex + 1, maps)
    }

    private fun parseMap2(group: Map.Entry<Int, List<String>>, maps: HashMap<Int, List<MapRange2>>) {
        group.value.forEach { line ->
            val values = line.split(" ")
            val destinationStart = values[0].toLong()
            val sourceStart = values[1].toLong()
            val length = values[2].toLong()

            val mapRange = MapRange2(
                sourceRange = LongRange(sourceStart, sourceStart + length - 1),
                transformation = destinationStart - sourceStart
            )
            maps[group.key] = maps[group.key].orEmpty() + mapRange
        }
    }

    private data class MapRange(val source: Long, val destination: Long, val length: Long)
    private data class MapRange2(val sourceRange: LongRange, val transformation: Long)
}
