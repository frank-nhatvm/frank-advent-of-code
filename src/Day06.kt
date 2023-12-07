import java.lang.Double.min
import java.lang.Exception
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun main() {


    fun String.lineToNum(): List<Long> = this.split(" ").filter { it.isNotBlank() }.map { it.toLong() }

    fun findAvailableHoldTime(t: Long, d: Long): Int {
        val delta = (t * t) - (4 * d)
        val squareRootOfDelta = sqrt(delta * 1.0)
        val x1 = (t - squareRootOfDelta) / 2
        val x2 = (t + squareRootOfDelta) / 2

        val x1ToInt = floor(x1).toInt()
        val x2ToInt = floor(x2).toInt()
        println("x1ToInt: $x1ToInt x2ToInt: $x2ToInt")
        
        var count = x2ToInt - x1ToInt - 1

        val checkX1 = (t - x1ToInt) * x1ToInt
        if (checkX1 > d) {
            count++
        }

        val checkX2 = (t - x2ToInt) * x2ToInt
        if (checkX2 > d) {
            count++
        }

        return count
    }


    fun part1(input: List<String>): Int {
        val time = input.first().split(":")[1].lineToNum()
        val distance = input[1].split(":")[1].lineToNum()

        val timeDistanceList = time.mapIndexed { index, t ->
            t to distance[index]
        }.map { (t, d) ->
            findAvailableHoldTime(t, d)
        }

        var result = 1
        timeDistanceList.forEach { result *= it }


        return result
    }

    fun part2(input: List<String>): Int {
        val time = input.first().split(":")[1].replace(" ", "").toLong()
        val distance = input[1].split(":")[1].replace(" ", "").toLong()
        println("time: $time distance: $distance")
        return findAvailableHoldTime(time, distance)
    }
//
//    // test if implementation meets criteria from the description, like:
    val testInput = readInput("data_test")
//    part1(testInput).println()

//    part2(testInput).println()

//
    val input = readInput("data")
//    part1(input).println()
    part2(input).println()
}
