package days

import utils.InputReader.readInput

object Day07 {

    private const val CARD_AND_BID_PATTERN = "^(.*) (\\d+)"

    fun puzzle1() {
        readInput("day07Input") { lines ->
            val cardValues = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')
            val cards = parseCards(lines.toList())
            val sortedCards = cards.sortByCard(cardValues).sortByHand()

            val totalWinnings = sortedCards.mapIndexed { index, (_, bid) -> bid.times(index + 1) }.sum()
            println("Day 7 puzzle 1 result is: $totalWinnings")
        }
    }

    private fun List<Pair<String, Int>>.sortByHand(): List<Pair<String, Int>> {
        return sortedBy { (hand, _) -> getHandPower(hand) }
    }

    private fun getHandPower(hand: String): Int {
        val chars = hand.toCharArray()
        val uniqueChars = chars.distinct()
        val repeatedAmounts = uniqueChars.mapNotNull {
            if (chars.filter { c -> c == it }.size > 1) {
                chars.count { c -> c == it }
            } else {
                null
            }
        }

        val power = when (repeatedAmounts.size) {
            0 -> 0
            2 -> {
                if (repeatedAmounts.sum() == 5) {
                    4
                } else {
                    2
                }
            }

            else -> {
                if (repeatedAmounts.first() == 2) {
                    repeatedAmounts.first() - 1
                } else {
                    if (repeatedAmounts.first() > 3) {
                        repeatedAmounts.first() + 1
                    } else {
                        repeatedAmounts.first()
                    }
                }
            }
        }

        return power
    }

    fun puzzle2() {
        readInput("day07Input") { lines ->
            val cardValues = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')
            val cards = parseCards(lines.toList())
            val sortedCards = cards.sortByCard(cardValues).sortByHand2()

            val totalWinnings = sortedCards.mapIndexed { index, (_, bid) -> bid.times(index + 1) }.sum()
            println("Day 7 puzzle 2 result is: $totalWinnings")
        }
    }

    private fun parseCards(lines: List<String>): List<Pair<String, Int>> {
        return lines.map { line ->
            val groups = Regex(CARD_AND_BID_PATTERN).find(line)?.groupValues
            val cards = groups?.get(1) ?: throw IllegalArgumentException("Must contain cards!")
            val bid = groups[2].toInt()

            cards to bid
        }
    }

    private fun List<Pair<String, Int>>.sortByCard(cardValues: List<Char>): List<Pair<String, Int>> {
        return sortedWith(
            compareBy(
                { cardValues.indexOf(it.first[0]) },
                { cardValues.indexOf(it.first[1]) },
                { cardValues.indexOf(it.first[2]) },
                { cardValues.indexOf(it.first[3]) },
                { cardValues.indexOf(it.first[4]) },
            )
        )
    }

    private fun List<Pair<String, Int>>.sortByHand2(): List<Pair<String, Int>> {
        return sortedBy { (hand, _) -> getHandPower2(hand) }
    }

    private fun getHandPower2(hand: String): Int {
        val chars = hand.toCharArray()
        val uniqueChars = chars.distinct()

        val repeatedAmounts = uniqueChars.mapNotNull {
            if (chars.filter { c -> c == it && c != 'J' }.size > 1) {
                chars.count { c -> it == c }
            } else {
                null
            }
        }.toMutableList()

        val jokers = chars.count { it == 'J' }

        val newMaxRepeatedAmount = if (repeatedAmounts.isNotEmpty()) {
            repeatedAmounts.max() + jokers
        } else {
            if (jokers < 5) {
                jokers + 1
            } else {
                jokers
            }
        }

        if (repeatedAmounts.isNotEmpty()) {
            repeatedAmounts[repeatedAmounts.indexOf(repeatedAmounts.max())] = newMaxRepeatedAmount
        } else {
            repeatedAmounts.add(newMaxRepeatedAmount)
        }

        val power = when (repeatedAmounts.size) {
            2 -> {
                if (repeatedAmounts.any { it > 3 }) {
                    repeatedAmounts.max() + 1
                } else {
                    if (repeatedAmounts.sum() == 5) {
                        4
                    } else {
                        2
                    }
                }
            }

            else -> {
                if (repeatedAmounts.first() == 2) {
                    repeatedAmounts.first() - 1
                } else {
                    if (repeatedAmounts.first() > 3) {
                        repeatedAmounts.first() + 1
                    } else if (repeatedAmounts.first() == 1) {
                        0
                    } else {
                        repeatedAmounts.first()
                    }
                }
            }
        }

        return power
    }
}
