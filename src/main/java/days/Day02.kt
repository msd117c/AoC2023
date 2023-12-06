package days

import utils.InputReader.readInput

object Day02 {

    private const val GAME_REGEX = "Game (\\d+):.*"
    private const val CUBE_QUANTITY_PATTERN = "(\\d+)"

    enum class Cube(val max: Int, val pattern: String) {
        RED(12, "(\\d+ red)"),
        GREEN(13, "(\\d+ green)"),
        BLUE(14, "(\\d+ blue)"),
    }

    fun puzzle1() {
        readInput("day02Input") { lines ->
            val games = lines.toList().map(::parseGame)
            val total = filterPossibleGames(games).sumOf { game -> game.id }

            println("Day 2 puzzle 1 result is: $total")
        }
    }

    private fun filterPossibleGames(games: List<Game>): List<Game> {
        return games.filter { game ->
            game.sets.all { set -> set.redAmount <= Cube.RED.max && set.greenAmount <= Cube.GREEN.max && set.blueAmount <= Cube.BLUE.max }
        }
    }

    fun puzzle2() {
        readInput("day02Input") { lines ->
            val games = lines.toList().map(::parseGame)
            val minAmountPerGame = games.map(::getMinimumAmountPerCube)
            val total = minAmountPerGame.sumOf { minAmount ->
                minAmount.minRed * minAmount.minGreen * minAmount.minBlue
            }

            println("Day 2 puzzle 2 result is: $total")
        }
    }

    private fun parseGame(line: String): Game {
        val gameId = Regex(GAME_REGEX).find(line)?.groupValues?.get(1) ?: throw IllegalArgumentException("Id not found")

        val subsetsLines = line.split(":")[1].split(";")
        val subsets = subsetsLines.map { entry ->
            val redCubes = Regex(Cube.RED.pattern).findAll(entry).getQuantityOfCubes()
            val greenCubes = Regex(Cube.GREEN.pattern).findAll(entry).getQuantityOfCubes()
            val blueCubes = Regex(Cube.BLUE.pattern).findAll(entry).getQuantityOfCubes()

            GameResult(redCubes, greenCubes, blueCubes)
        }

        return Game(id = gameId.toInt(), sets = subsets)
    }

    private fun Sequence<MatchResult>.getQuantityOfCubes(): Int {
        val entry = toList().firstOrNull()?.groups?.firstOrNull()?.value.orEmpty()

        return Regex(CUBE_QUANTITY_PATTERN).find(entry)?.groups?.firstOrNull()?.value?.toInt() ?: 0
    }

    private fun getMinimumAmountPerCube(game: Game): MinimumAmounts {
        val minRed = game.sets.maxOf { it.redAmount }
        val minGreen = game.sets.maxOf { it.greenAmount }
        val minBlue = game.sets.maxOf { it.blueAmount }

        return MinimumAmounts(minRed, minGreen, minBlue)
    }

    private data class Game(val id: Int, val sets: List<GameResult>)
    private data class GameResult(val redAmount: Int, val greenAmount: Int, val blueAmount: Int)
    private data class MinimumAmounts(val minRed: Int, val minGreen: Int, val minBlue: Int)
}
