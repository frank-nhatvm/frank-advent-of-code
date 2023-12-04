import kotlin.math.pow

data class Card(val cardId: Int, val listWinning: List<Int>, val listMyNums: List<Int>) {
    companion object {
        fun from(string: String): Card {

            val (cardIdInfo, data) = string.split(":")

            val cardId = cardIdInfo.trim().split(" ").filter { it.isNotEmpty() }[1].trim().toInt()

            val (winningData, myNumData) = data.split("|")
//            println(string)
//            println(cardId)
//            println(winningData)
//            println(myNumData)

            val listWinning = winningData.trim().split(" ").filter { it.isNotEmpty() }.map { it.trim().toInt() }

            val listMyNums = myNumData.trim().split(" ").filter { it.isNotEmpty() }.map {
                it.trim().toInt()
            }

            return Card(cardId, listWinning, listMyNums)
        }
    }

    fun getWorthPoint(): Int {
        val matchesPoint = listMyNums.filter { listWinning.contains(it) }
        val size = matchesPoint.size
        return if (size == 0) {
            0
        } else if (size == 1) {
            1
        } else {
            2.toDouble().pow((size - 1).toDouble()).toInt()
        }
    }

    fun matchingNumber(): Int {
        return listMyNums.filter { listWinning.contains(it) }.size
    }

}

fun main() {
    fun part1(input: List<String>): Int {

        val cards = input.map { Card.from(it) }

        return cards.sumOf { it.getWorthPoint() }
    }

    fun part2(input: List<String>): Int {

        val cards = input.map { Card.from(it) }

        val hmCardMatchingNum = cards.associate { it.cardId to it.matchingNumber() } // card id and count of number in list of winning

        val listCardIds = hmCardMatchingNum.keys

        val hmCardAndInstance = mutableMapOf<Int, Int>()

        listCardIds.forEach { id ->
            val matchingNum = hmCardMatchingNum[id] ?: 0

            val count = hmCardAndInstance[id] ?: 0
            hmCardAndInstance[id] = count + 1

            if (matchingNum > 0) {
                for (i in 1..matchingNum) {
                    val copyId = id + i
                    val copyCardCount = hmCardMatchingNum[copyId]
                    if (copyCardCount != null) {
                        val copyCount = hmCardAndInstance[copyId] ?: 0
                        hmCardAndInstance[copyId] = copyCount + (count + 1)
                    }
                }
            }
          //  println("id: $id : $hmCardAndInstance")
        }

     //   println(hmCardAndInstance)
        return hmCardAndInstance.values.sumOf { it }

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("data_test")
    println(part2(testInput))

    val input = readInput("data")
    //  part1(input).println()
    part2(input).println()
}
