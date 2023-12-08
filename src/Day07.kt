enum class CardLabel(val label: String, val value: Int) {
    CA("A", 14),
    CK("K", 13),
    CQ("Q", 12),
    CJ("J", 11),
    CT("T", 10),
    C9("9", 9),
    C8("8", 8),
    C7("7", 7),
    C6("6", 6),
    C5("5", 5),
    C4("4", 4),
    C3("3", 3),
    C2("2", 2);

    companion object {
        fun from(string: String) =
            entries.associateBy { it.label }[string] ?: throw Exception("No CardLabel for $string")
    }

}

enum class CardHandType(val rank: Int) {
    FIVE_KIND(6),
    FOUR_KIND(5),
    FULL_HOUSE(4),
    THREE_KIND(3),
    TWO_PAIR(2),
    ONE_PAIR(1),
    HIGH_CARD(0);
}


data class CardHand(val listCard: List<CardLabel>, val bid: Int) {

    private val cardHandType: CardHandType
        get() {
            val groupCardTypes = listCard.groupBy { it.label }.map { it.value.count() }.sortedDescending()
            return when (groupCardTypes.count()) {
                1 -> CardHandType.FIVE_KIND
                2 -> {
                    if (groupCardTypes.first() == 4) {
                        CardHandType.FOUR_KIND
                    } else {
                        CardHandType.FULL_HOUSE
                    }
                }

                3 -> {
                    if (groupCardTypes.first() == 3) {
                        CardHandType.THREE_KIND
                    } else {
                        CardHandType.TWO_PAIR
                    }
                }

                4 -> CardHandType.ONE_PAIR
                else -> CardHandType.HIGH_CARD
            }

        }

    private fun cardHandTypeValue(): Int {

        val jCardCount = listCard.count { it == CardLabel.CJ }
        val otherCardGroup = listCard.filter { it != CardLabel.CJ }.groupBy { it.label }
        val otherCardGroupCount = otherCardGroup.count()

        val newCardHandType = when (jCardCount) {
            5 -> {
                CardHandType.FIVE_KIND
            }

            4 -> {
                // 2JJJJ ->11111
                CardHandType.FIVE_KIND
            }

            3 -> {
                if (otherCardGroupCount == 1) {
                    // 22JJJ -> 22222
                    CardHandType.FIVE_KIND
                } else {
                    // 32JJJ -> 32222
                    CardHandType.FOUR_KIND
                }
            }

            2 -> {
                when (otherCardGroupCount) {
                    1 -> {
                        // 333JJ -> 33333
                        CardHandType.FIVE_KIND
                    }

                    2 -> {
                        // 344JJ -> 34444
                        CardHandType.FOUR_KIND
                    }

                    else -> {
                        // 345JJ -> 34555
                        CardHandType.THREE_KIND
                    }
                }
            }

            1 -> {
                when (otherCardGroupCount) {
                    1 -> {
                        // 9999J -> 99999
                        CardHandType.FIVE_KIND
                    }

                    2 -> {

                        if (otherCardGroup.values.first().size == 2) {
                            // 8899J -> 88999
                            CardHandType.FULL_HOUSE
                        } else {
                            // QQQJA -> QQQQA
                            CardHandType.FOUR_KIND
                        }
                    }

                    3 -> {
                        // 2344J -> 23444
                        CardHandType.THREE_KIND
                    }

                    else -> {
                        // 4
                        // 3456J -> 34566
                        CardHandType.ONE_PAIR
                    }
                }
            }

            else -> cardHandType
        }

        //  println("jCardCount: $jCardCount - otherCardGroupCount: $otherCardGroupCount - otherCardGroup: $otherCardGroup - oldCardType: ${cardHandType.name} - newCardType: ${newCardHandType.name}")
        return newCardHandType.rank

    }

    fun comparePart2(otherCardHand: CardHand): Int {
        if (cardHandTypeValue() != otherCardHand.cardHandTypeValue()) {
            return cardHandTypeValue() - otherCardHand.cardHandTypeValue()
        }

        return compare2ListCardPart2(listCard, otherCardHand.listCard)
    }

    fun compare(otherCardHand: CardHand): Int {

        if (cardHandType != otherCardHand.cardHandType) {
            return cardHandType.rank - otherCardHand.cardHandType.rank
        }
        //same card hand type

        return compare2ListCard(listCard, otherCardHand.listCard)
    }

    private fun compare2ListCardPart2(firstListCard: List<CardLabel>, secondListCard: List<CardLabel>): Int {

        if (firstListCard.size != secondListCard.size) {
            throw Exception("no same size to compare")
        }

        firstListCard.forEachIndexed { index, firstCardLabel ->
            val secondCardLabel = secondListCard[index]
            val firstCardLabelValue = if (firstCardLabel == CardLabel.CJ) 1 else firstCardLabel.value
            val secondCardLabelValue = if (secondCardLabel == CardLabel.CJ) 1 else secondCardLabel.value

            if (firstCardLabelValue != secondCardLabelValue) {
                return firstCardLabelValue - secondCardLabelValue
            }
        }


        return 0
    }

    private fun compare2ListCard(firstListCard: List<CardLabel>, secondListCard: List<CardLabel>): Int {

        if (firstListCard.size != secondListCard.size) {
            throw Exception("no same size to compare")
        }

        firstListCard.forEachIndexed { index, firstcardLabel ->
            val secondCardLabel = secondListCard[index]
            if (secondCardLabel.value != firstcardLabel.value) {
                return firstcardLabel.value - secondCardLabel.value
            }
        }


        return 0
    }

    override fun toString(): String {
        val cards = listCard.joinToString("") { it.label }
        return "card: $cards - bid:$bid - cardHandType: ${cardHandType.rank} - cardHandTypeValue: ${cardHandTypeValue()}"
    }

    companion object {
        fun from(string: String): CardHand {
            val (stringCardLabels, stringBid) = string.split(" ")
            val cardLabels = stringCardLabels.map { CardLabel.from(it.toString()) }
            return CardHand(cardLabels, stringBid.toInt())
        }
    }

}

fun main() {


    fun part1(input: List<String>): Int {
        var result = 0
        input.map { CardHand.from(it) }.sortedWith(Comparator { firstCard: CardHand, secondCard: CardHand ->
            return@Comparator firstCard.compare(secondCard)
        }).forEachIndexed { index, cardHand ->
            // println("bid: ${cardHand.bid} index ${index+1}")
            result += (index + 1) * cardHand.bid
        }

        return result
    }

    fun part2(input: List<String>): Int {

        var result = 0
        input.map { CardHand.from(it) }.sortedWith(Comparator { firstCard: CardHand, secondCard: CardHand ->
            return@Comparator firstCard.comparePart2(secondCard)
        }).forEachIndexed { index, cardHand ->
            //  println("$cardHand - index $index")
            result += (index + 1) * cardHand.bid
        }

        return result
    }
//
//    // test if implementation meets criteria from the description, like:
    val testInput = readInput("data_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("data")
    part1(input).println()
    part2(input).println()
}
