package days

import utils.InputReader.readInput
import java.util.Collections.swap

object Day14 {

    fun puzzle1() {
        readInput("day14Input") { lines ->
            val columns = lines.toList().columns2()

            columns.moveRocksToNorth()
            val load = columns.getLoad()
            columns.drawMap()
            println("Day 14 puzzle 1 result is: $load")
        }
    }

    private fun Array<CharArray>.getLoad(): Int {
        return sumOf { column ->
            column.mapIndexed { index, c ->
                val weight = size - index
                if (c == 'O') weight else 0
            }.sum()
        }
    }

    private fun List<String>.columns2(): Array<CharArray> {
        return first().mapIndexed { x, _ ->
            map { line -> line[x] }.toCharArray()
        }.toTypedArray()
    }

    private fun Array<CharArray>.moveRocksToNorth() {
        var canMove = true
        while (canMove) {
            canMove = false
            forEachIndexed { x, column ->
                column.forEachIndexed { y, c ->
                    if (y > 0) {
                        val previous = this[x][y - 1]
                        val currentSymbol = this[x][y]

                        if (previous == '.' && currentSymbol == 'O') {
                            canMove = true
                            this[x][y] = '.'
                            this[x][y - 1] = 'O'
                        }
                    }
                }
            }
        }
    }

    private fun Array<CharArray>.drawMap() {
        indices.forEach { x ->
            forEach { column -> print(column[x]) }
            println()
        }
    }

    enum class Direction {
        NORTH,
        WEST,
        SOUTH,
        EAST
    }

    fun puzzle2() {
        readInput("day14Input") { lines ->
            val columns = lines.toList().columns2()

            columns.cycle()

            val load = columns.getLoad()
            println("Day 14 puzzle 2 result is: $load")
        }
    }

    private fun List<String>.columns(): List<String> {
        return first().mapIndexed { x, _ ->
            map { line -> line[x] }.joinToString("")
        }
    }

    private fun Array<CharArray>.cycle() {
        moveRocksTo2(Direction.NORTH)
        moveRocksTo2(Direction.WEST)
        moveRocksTo2(Direction.SOUTH)
        moveRocksTo2(Direction.EAST)
    }

    private fun Array<CharArray>.moveRocksTo2(direction: Direction) {
        var canMove = true

        when (direction) {
            Direction.NORTH, Direction.SOUTH -> {
                while (canMove) {
                    canMove = false
                    forEachIndexed { x, column ->
                        column.forEachIndexed { y, c ->
                            if (direction == Direction.NORTH) {
                                if (y > 0) {
                                    val previous = this[x][y - 1]
                                    val currentSymbol = this[x][y]

                                    if (previous == '.' && currentSymbol == 'O') {
                                        canMove = true
                                        this[x][y] = '.'
                                        this[x][y - 1] = 'O'
                                    }
                                }
                            }
                            if (direction == Direction.SOUTH) {
                                if (y < column.size - 1) {
                                    val next = this[x][y + 1]
                                    val currentSymbol = this[x][y]

                                    if (next == '.' && currentSymbol == 'O') {
                                        canMove = true
                                        this[x][y] = '.'
                                        this[x][y + 1] = 'O'
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Direction.EAST, Direction.WEST -> {
                while (canMove) {
                    canMove = false
                    first().indices.forEach { y ->
                        map { it[y] }.joinToString("").forEachIndexed { x, _ ->
                            if (direction == Direction.EAST) {
                                if (x > 0) {
                                    val previous = this[x - 1][y]
                                    val currentSymbol = this[x][y]

                                    if (previous == '.' && currentSymbol == 'O') {
                                        canMove = true
                                        this[x][y] = '.'
                                        this[x - 1][y] = 'O'
                                    }
                                }
                            } else {
                                if (y < size - 1) {
                                    val next = this[x + 1][y]
                                    val currentSymbol = this[x][y]

                                    if (next == '.' && currentSymbol == 'O') {
                                        canMove = true
                                        this[x][y] = '.'
                                        this[x + 1][y] = 'O'
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun List<String>.moveRocksTo(direction: Direction): List<String> {
        return when (direction) {
            Direction.NORTH, Direction.SOUTH -> {
                map { column ->
                    column.split("#").joinToString("#") { section ->
                        val charArray = section.toCharArray().toMutableList()
                        if (direction == Direction.NORTH) {
                            quicksort(charArray, 0, charArray.size - 1, reversed = false)
                        } else {
                            quicksort(charArray, 0, charArray.size - 1, reversed = true)
                        }
                        charArray.joinToString("")
                    }
                }
            }

            Direction.EAST, Direction.WEST -> {
                rows().map { column ->
                    column.split("#").joinToString("#") { section ->
                        val charArray = section.toCharArray().toMutableList()
                        if (direction == Direction.EAST) {
                            quicksort(charArray, 0, charArray.size - 1, reversed = true)
                        } else {
                            quicksort(charArray, 0, charArray.size - 1, reversed = false)
                        }
                        charArray.joinToString("")
                    }
                }.columns()
            }
        }
    }

    private fun List<String>.rows(): List<String> {
        return first().mapIndexed { y, _ ->
            map { line -> line[y] }.joinToString("")
        }
    }

    private fun List<String>.getLoad(): Int {
        return sumOf { column ->
            column.mapIndexed { index, c ->
                val weight = size - index
                if (c == 'O') weight else 0
            }.sum()
        }
    }

    private fun partition(a: MutableList<Char>, start: Int, end: Int, reversed: Boolean): Int {
        // Pick the rightmost element as a pivot from the array
        val pivot = a[end]

        // elements less than the pivot will be pushed to the left of `pIndex`
        // elements more than the pivot will be pushed to the right of `pIndex`
        // equal elements can go either way
        var pIndex = start

        // each time we find an element less than or equal to the pivot,
        // `pIndex` is incremented, and that element would be placed
        // before the pivot.
        for (i in start until end) {
            if (reversed) {
                if (a[i] <= pivot) {
                    swap(a, i, pIndex)
                    pIndex++
                }
            } else {
                if (a[i] >= pivot) {
                    swap(a, i, pIndex)
                    pIndex++
                }
            }
        }

        // swap `pIndex` with pivot
        swap(a, end, pIndex)


        // return `pIndex` (index of the pivot element)
        return pIndex
    }

    // Quicksort routine
    private fun quicksort(a: MutableList<Char>, start: Int, end: Int, reversed: Boolean) {
        // base condition
        if (start >= end) return

        // rearrange elements across pivot
        val pivot = partition(a, start, end, reversed)

        // recur on subarray containing elements less than the pivot
        quicksort(a, start, pivot - 1, reversed)

        // recur on subarray containing elements more than the pivot
        quicksort(a, pivot + 1, end, reversed)
    }

    private fun List<String>.drawMap() {
        indices.forEach { x ->
            forEach { column -> print(column[x]) }
            println()
        }
    }
}
