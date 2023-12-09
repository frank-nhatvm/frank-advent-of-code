fun main() {


    fun List<Long>.substract(): Long {
        var result = 0L
        this.reversed().forEach {
            result = it - result
        }
        return result
    }

    fun findNextNum(list: List<Long>, result: List<Long>, isTakeLast: Boolean): List<Long> {
        val maxIndex = list.size - 2
        val newList = mutableListOf<Long>()
        for (index in 0..maxIndex) {
            val nextIndex = index + 1
            newList.add(list[nextIndex] - list[index])
        }
        val newResult = result.toMutableList()
        val newElement = if (isTakeLast) newList.last() else newList.first()
        newResult.add(newElement)
        //   println("newList: $newList - newResult: $newResult")
        return if (newList.toSet().size == 1) {
            // done
            newResult
        } else {
            findNextNum(newList, newResult, isTakeLast)
        }
    }

    fun List<String>.parse(): List<List<Long>> = map { line ->
        line.split(" ").map { it.toLong() }
    }

    fun part1(input: List<String>): Long {
        return input.parse().sumOf { line ->
            findNextNum(line, listOf(line.last()), isTakeLast = true).sum()
        }
    }

    fun part2(input: List<String>): Long {
        return input.parse().sumOf { line ->
            findNextNum(line, listOf(line.first()), isTakeLast = false).substract()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("data_test")
//    part1(testInput).println()
 //   part2(testInput).println()

    val input = readInput("data")
//    part1(input).println()
    part2(input).println()
}
