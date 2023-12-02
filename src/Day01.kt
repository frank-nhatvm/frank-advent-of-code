import java.lang.Exception

fun main() {

    val numInTexts = hashMapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
    )

    fun checkLine(line: String): Int {
        var firstNum: Int = -1
        var lastNum: Int = -1

        val chars = line.toCharArray().toList()

        chars.forEach { c ->
            if (c.isDigit()) {
                val cInt = c.digitToInt()
                if (firstNum == -1) {
                    firstNum = cInt
                    lastNum = cInt
                } else {
                    lastNum = cInt
                }
            }
        }

        val calibrationValue = "$firstNum$lastNum"
        return calibrationValue.toInt()
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { checkLine(it) }
    }


    fun find(s1: String, numInText: String): List<Int> {

        //5
        // hstmcsevensix
        // seven : 5
        // six and seven

        val listIndex = mutableListOf<Int>()
        if (s1.contains(numInText)) {
            val numInTextSize = numInText.length
            val firstChar = numInText.first()
            s1.forEachIndexed { index, c ->
                if (c == firstChar) {
                    val testLastIndex = index + (numInTextSize - 1)
                    if (testLastIndex < (s1.length)) {
                        val test = s1.substring(index, testLastIndex +1)
                        if (test == numInText) {
                            listIndex.add(index)
                        }
                    }
                }

            }

        }

        return listIndex
    }

    fun findNumTextInString(string: String): Map<Int, Int> {
        val positionAndNum = mutableMapOf<Int, Int>()
        numInTexts.forEach { (text, num) ->
            val positions = find(string, text)
            if(positions.isNotEmpty()){
                positions.forEach {
                    positionAndNum[it] = num
                }
            }
        }
        return positionAndNum
    }

    fun checkLine2(line: String): Int {

        val chars = line.toCharArray().toList()

        var firstNum = -1
        var lastNum = -1

        val positionAndNum = mutableMapOf<Int, Int>()

        chars.forEachIndexed { index, c ->
            if (c.isDigit()) {
                positionAndNum[index] = c.digitToInt()
            }
        }

        if (positionAndNum.isEmpty()) {

            val numPositions = findNumTextInString(line)
            val sortedKey = numPositions.keys.sorted()
            numPositions[sortedKey.first()]?.let {
                firstNum = it
            }
            numPositions[sortedKey.last()]?.let {
                lastNum = it
            }
        } else {
            val sortedPosition = positionAndNum.keys.sorted()
            val indexOfFirstDigit = sortedPosition.first()
            val indexOfLastDigit = sortedPosition.last()

            firstNum = positionAndNum[indexOfFirstDigit]!!

            if (indexOfFirstDigit != 0) {
                val firstSubChars = chars.subList(0, indexOfFirstDigit).joinToString("")
                val numPositionsInFirst = findNumTextInString(firstSubChars)
                if (numPositionsInFirst.isNotEmpty()) {
                    val index = numPositionsInFirst.keys.minOf { it }
                    numPositionsInFirst[index]?.let {
                        firstNum = it
                    }
                }
            }

            lastNum = positionAndNum[indexOfLastDigit]!!
            if (indexOfLastDigit != chars.size - 1) {
                val lastSubChars = chars.subList(indexOfLastDigit + 1, chars.size).joinToString("")
                val numPositionsInLast = findNumTextInString(lastSubChars)
                if (numPositionsInLast.isNotEmpty()) {
                    val index = numPositionsInLast.keys.maxOf { it }
                    numPositionsInLast[index]?.let {
                        lastNum = it
                    }
                }
            }
        }

        if (firstNum == -1 || lastNum == -1) {
            throw Exception("Wrong")
        }

        return "$firstNum$lastNum".toInt().also { println("$line - $it") }

    }

    fun part2(input: List<String>): Int {

        return input.sumOf { checkLine2(it) }
    }

//   println(checkLine2("81eighttwoqdjkmnleightdbmzz "))// 82
//    println(checkLine2("eightsixeight3ninetwonine "))// 89

//    val testInput2 = readInput("Day01_part2_test")
//    println("Test Input 2: ${part2(testInput2)}")
//
   val part2Input = readInput("Day01_part2")
    println("Part 2: ${part2(part2Input)}")

    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("Day01_test")
//    val part1TestValue = part1(testInput)

//    val input = readInput("Day01_part1")
//    part1(input).println()
//    part2(input).println()
}
