data class CharacterNumLine(val num: Int, val position: Int)

data class NumLine(val characterNumLine: List<CharacterNumLine>) {

    fun isNearAStart(starPosition: Int): Boolean{
        if(characterNumLine.isEmpty()){
            return false
        }

        val sorted = characterNumLine.sortedBy { it.position }

        val firstCharacterNumPosition  = sorted.first().position
        val lastCharacterNumPosition = sorted.last().position

        val rangePosition = (firstCharacterNumPosition-1 .. lastCharacterNumPosition+1)
        return  rangePosition.contains(starPosition)
    }

    fun isValid(
        previousSymbolPositions: List<Int>,
        nextSymbolPositions: List<Int>,
        currentLineSymbolPosition: List<Int>
    ): Boolean {
        val sorted = characterNumLine.sortedBy { it.position }
        sorted.forEach { characterNumLine ->
            val pos = characterNumLine.position
            val positionToCheck = positionToCheck(pos)
            if (previousSymbolPositions.containAtLeastOne(positionToCheck) || nextSymbolPositions.containAtLeastOne(
                    positionToCheck
                )
            ) {
                return true
            }
            val currentPositionToCheck = listOf(pos - 1, pos + 1)
            if (currentLineSymbolPosition.containAtLeastOne(currentPositionToCheck)) {
                return true
            }
        }

        return false
    }

    fun toNum(): Int {
        return characterNumLine.sortedBy { it.position }.map { it.num }.joinToString("").toInt()//.also { println(it) }
    }

    private fun positionToCheck(position: Int) = listOf(position - 1, position, position + 1)

    private fun List<Int>.containAtLeastOne(other: List<Int>): Boolean {
        other.forEach {
            if (this.contains(it)) {
                return true
            }
        }
        return false
    }
}

data class Line(val numLines: List<NumLine>, val symbolPosition: List<Int>, val linePosition: Int, val startPositions: List<Int>) {
    companion object {

        private val symbols = listOf('-', '+', '*', '@', '=', '#', '/', '$', '%', '&')
        fun from(position: Int, line: String): Line {

            val listNumLines = mutableListOf<NumLine>()
            val listSymbolPosition = mutableListOf<Int>()
            val startPositions = mutableListOf<Int>()

            val tempListCharacterNumLine = mutableListOf<CharacterNumLine>()
            line.forEachIndexed { index, char ->
                if (symbols.contains(char)) {
                    if(char == '*'){
                        startPositions.add(index)
                    }
                    listSymbolPosition.add(index)
                } else if (char.isDigit()) {

                    val characterNumLine = CharacterNumLine(char.digitToInt(), index)
                    tempListCharacterNumLine.add(characterNumLine)

                    if (index == line.length -1){
                        val numLine = NumLine(tempListCharacterNumLine.toList())
                        listNumLines.add(numLine)
                        tempListCharacterNumLine.clear()
                    }
                    else {
                        val nextIndex = index + 1
                        if (nextIndex < line.length) {
                            val nextChar = line[nextIndex]
                            if (!nextChar.isDigit()) {
                                val numLine = NumLine(tempListCharacterNumLine.toList())
                                listNumLines.add(numLine)
                                tempListCharacterNumLine.clear()
                            }
                        }
                    }
                }
            }
            return Line(
                numLines = listNumLines.toList(),
                symbolPosition = listSymbolPosition.toList(),
                linePosition = position,
                startPositions = startPositions
            )
        }
    }

    fun gearRatio(previousLine: Line?, nextLine: Line?): Int{

        if(startPositions.isEmpty()){
            return 0
        }

        val numInPreviousLine = previousLine?.numLines ?: emptyList()
        val numInNextLine = nextLine?.numLines ?: emptyList()

        val allNumToCheck = mutableListOf<NumLine>()
        allNumToCheck.addAll(numInPreviousLine)
        allNumToCheck.addAll(numInNextLine)
        allNumToCheck.addAll(numLines)


       return startPositions.sumOf { starPosition ->
            val listNeighborNum = allNumToCheck.filter { it.isNearAStart(starPosition) }
          if(listNeighborNum.size == 2){
              listNeighborNum.first().toNum() * listNeighborNum.last().toNum()
          }else{
              0
          }
        }
    }

    fun listPartNum(previousLine: Line?, nextLine: Line?): List<NumLine> {
        val previousSymbolPositions = previousLine?.positionsToCheck() ?: emptyList()
        val nextSymbolPositions = nextLine?.positionsToCheck() ?: emptyList()
        return numLines.filter { it.isValid(previousSymbolPositions, nextSymbolPositions, symbolPosition) }
    }

    private fun positionsToCheck(): List<Int> {
        val numPositions =
            numLines.flatMap { it.characterNumLine.map { characterNumLine -> characterNumLine.position } }
                .toMutableList()
        numPositions.addAll(symbolPosition)
        return numPositions.toList()
    }

}


fun main() {

    fun List<Line>.previousNextLinesFor(index: Int): Pair<Line?, Line?>{
      return   if (index == 0) {
            Pair(null, this[1])
        } else if (index == (this.size - 1)) {
          Pair(this[index - 1], null)
        } else {
          Pair(this[index - 1], this[index + 1])
        }
    }

    fun part1(input: List<String>): Int {

        val lines = input.mapIndexed { index, s -> Line.from(index, s) }

        if (lines.isEmpty()) {
            return 0
        }

        if (lines.size == 1) {
            return lines.first().listPartNum(null, null).sumOf { it.toNum() }
        }

        val numLines = lines.flatMapIndexed { index, line ->

            val (previousLine, nextLine) = lines.previousNextLinesFor(index)

            line.listPartNum(previousLine, nextLine)

        }


        return numLines.sumOf { it.toNum() }
    }

    fun part2(input: List<String>): Int {
        val lines = input.mapIndexed { index, s -> Line.from(index, s) }

        var sum = 0
        lines.forEachIndexed { index, line ->
            val (previousLine, nextLine) = lines.previousNextLinesFor(index)
            sum += line.gearRatio(previousLine, nextLine)
        }

        return sum
    }

    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("data_test")
//    println(part2(testInput))
//    println(part1(testInput))



    val input = readInput("data")
    part1(input).println()
    part2(input).println()
}
