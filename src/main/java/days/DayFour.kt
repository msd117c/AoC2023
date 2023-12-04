package days

import utils.InputReader.readInput

object DayFour {

    private const val LINE_PATTERN = "^Card ?\\s+\\d+: (.*)"
    fun puzzle1() {
        readInput("dayFourInput") { lines ->
            val cards = lines.toList().map(::getCardNumbers)
            val pointsList = cards.map(::getPoints)

            val totalPoints = pointsList.sum()
            println("Day 4 puzzle 1 result is: $totalPoints")
        }
    }

    private fun getPoints(card: Card): Int {
        var points = 0

        card.winningNumbers.forEach { number ->
            if (card.yourNumbers.contains(number)) {
                points = if (points == 0) 1 else points.times(2)
            }
        }

        return points
    }

    fun puzzle2() {
        readInput("dayFourInput") { lines ->
            var cards = lines.toList().map(::getCardNumbers)

            cards.indices.forEach { index ->
                cards = updateCardList(cards[index], index, cards)
            }

            val totalInstances = cards.sumOf { it.copies }
            println("Day 4 puzzle 2 result is: $totalInstances")
        }
    }

    private fun getCardNumbers(line: String): Card {
        val group = Regex(LINE_PATTERN).matchEntire(line)

        group?.groups?.get(1)?.let { result ->
            val numbers = result.value.split("|")
            val winningNumbers = numbers[0].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }
            val yourNumbers = numbers[1].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }

            return Card(winningNumbers, yourNumbers, copies = 1)
        } ?: throw IllegalArgumentException("Each line must contain a card!")
    }

    private fun updateCardList(card: Card, index: Int, cardList: List<Card>): List<Card> {
        var matches = 0

        card.winningNumbers.forEach { number ->
            if (card.yourNumbers.contains(number)) matches++
        }

        val cardsToRepeat = cardList.subList(index + 1, index + 1 + matches)

        return cardList.toMutableList().apply {
            cardsToRepeat.forEach { cardToRepeat ->
                set(indexOf(cardToRepeat), cardToRepeat.copy(copies = cardToRepeat.copies + card.copies))
            }
        }
    }

    private data class Card(val winningNumbers: List<Int>, val yourNumbers: List<Int>, val copies: Int)
}
