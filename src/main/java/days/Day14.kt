package days

import utils.InputReader.readInput

object Day14 {

    enum class Direction {
        NORTH,
        WEST,
        SOUTH,
        EAST
    }

    fun puzzle1() {
        readInput("day14Input") { lines ->
            val columns = lines.toList().columns()

            columns.moveRocksTo(Direction.NORTH)
            val load = columns.getLoad()
            println("Day 14 puzzle 1 result is: $load")
        }
    }

    fun puzzle2() {
        readInput("day14Input") { lines ->
            val columns = lines.toList().columns()

            val load = columns.test()

            println("Day 14 puzzle 2 result is: $load")
        }
    }

    private fun List<String>.columns(): Array<CharArray> {
        return first().mapIndexed { x, _ ->
            map { line -> line[x] }.toCharArray()
        }.toTypedArray()
    }

    private fun Array<CharArray>.test(): Int {
        val total = 1000000000

        val cache = mutableMapOf<String, Int>()

        repeat(total) { cycle ->
            val key = joinToString("") { it.joinToString("") }

            if (key in cache) {
                val length = cycle - cache.getValue(key)
                val remainingCycles = (total - cycle).mod(length)
                repeat(remainingCycles) { cycle() }
                return getLoad()
            }

            cache[key] = cycle
            cycle()
        }

        return getLoad()
    }

    private fun Array<CharArray>.cycle() {
        moveRocksTo(Direction.NORTH)
        moveRocksTo(Direction.WEST)
        moveRocksTo(Direction.SOUTH)
        moveRocksTo(Direction.EAST)
    }

    private fun Array<CharArray>.moveRocksTo(direction: Direction) {
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
                        indices.forEach { x ->
                            if (direction == Direction.EAST) {
                                if (x < size - 1) {
                                    val next = this[x + 1][y]
                                    val currentSymbol = this[x][y]

                                    if (next == '.' && currentSymbol == 'O') {
                                        canMove = true
                                        this[x][y] = '.'
                                        this[x + 1][y] = 'O'
                                    }
                                }
                            } else {
                                if (x > 0) {
                                    val previous = this[x - 1][y]
                                    val currentSymbol = this[x][y]

                                    if (previous == '.' && currentSymbol == 'O') {
                                        canMove = true
                                        this[x][y] = '.'
                                        this[x - 1][y] = 'O'
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
}
