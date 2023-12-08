import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder
import kotlin.math.abs

enum class Direction(val direction: String) {
    LEFT("L"),
    RIGHT("R");

    companion object {
        fun from(string: String) = entries.associateBy { it.direction }[string] ?: throw Exception("Invalid direction")
    }
}

data class Node(val left: String, val right: String) {
    fun valueFor(direction: Direction) = if (direction == Direction.LEFT) left else right

    override fun toString(): String {
        return "left: $left - right: $right"
    }

    fun same(other: Node): Boolean {
        return left == other.left && right == other.right
    }

    fun leftRight() = "$left$right"

    companion object {
        fun from(string: String): Node {
            // (BBB, CCC)
            val (left, right) = string.replace("(", "").replace(")", "").replace(" ", "").split(",")
            return Node(left, right)
        }
    }
}

fun main() {

    fun countSteps(
        startNode: String,
        endNodes: List<String>,
        instructions: List<Direction>,
        hmData: Map<String, Node>
    ): Long {

        val instructionSize = instructions.size
        var indexInstruction = 0

        var tmpNode = startNode
        var steps = 0L

        while (!endNodes.contains(tmpNode)) {
            val node = hmData[tmpNode]
            if (node != null) {
                steps++
                val direction = instructions[indexInstruction]
                tmpNode = node.valueFor(direction)

                indexInstruction++
                if (indexInstruction > instructionSize - 1) {
                    indexInstruction = 0
                }
            } else {
                // tmpNode = endNode
                throw Exception("Can not find the path to finsih")
            }
        }

        return steps
    }

    fun List<String>.parse(): Pair<List<Direction>, Map<String, Node>> {
        val instructions = mutableListOf<Direction>()
        val hmData = mutableMapOf<String, Node>()

        this.forEach { line ->
            if (line.isNotBlank()) {
                if (line.contains("=")) {
                    val (nodeName, dataString) = line.split(" = ")
                    hmData[nodeName] = Node.from(dataString)
                } else {
                    line.forEach { instructions.add(Direction.from(it.toString())) }
                }
            }
        }
        return Pair(instructions.toList(), hmData.toMap())
    }

    fun part1(input: List<String>): Long {
        val (instructions, hmData) = input.parse()
        return countSteps(startNode = "AAA", endNodes = listOf("ZZZ"), instructions = instructions, hmData = hmData)

    }

    infix fun List<String>.same(other: List<String>): Boolean {
        if (this.size != other.size) {
            return false
        }

        val firstSet = this.toSet()
        val secondSet = other.toSet()

        if (firstSet.size != secondSet.size) {
            return false
        }

        return firstSet == secondSet
    }


    fun gcd(a: Long, b: Long): Long {
        return if (b == 0L) a else gcd(b, a % b)
    }

    fun lcm(a: Long, b: Long) : Long {
        return  abs(a * b)/gcd(a,b)
    }

    fun findLCM(list: List<Long>):Long{
        var result = 1L
        for (num in list){
            result = lcm(result,num)
        }
        return result
    }



    fun part2(input: List<String>): Long {

        val (instructions, hmData) = input.parse()

        val listStartNodes = hmData.keys.filter { it.endsWith("A") }
        val listEndNodes = hmData.keys.filter { it.endsWith("Z") }

        val listSteps = listStartNodes.map { startNode ->
            countSteps(startNode = startNode, endNodes = listEndNodes, instructions = instructions, hmData = hmData)
        }
        listSteps.println()
        val result =  findLCM(listSteps)

        listSteps.map { it ->
            println("$it - ${result/it}")
        }

        return result

//
//        println("listStartNodes: $listStartNodes")
//        println("listEndNodes: $listEndNodes")

//        var isFinish = false
//        var instructionIndex = 0
//        val instructionSize = instructions.size
//        var steps = 0
//        var tmpStartNodes = listStartNodes
//        println("Start: ${tmpStartNodes.joinToString("-")}")
//        var countTrick = 50
//        while (!isFinish) {
//            steps++
//            val direction = instructions[instructionIndex]
//            val tmpEndNodes = tmpStartNodes.mapNotNull { node -> hmData[node]?.valueFor(direction) }
//            val nodesEndWithZ = tmpEndNodes.filter { it.endsWith("Z") }
//            if (nodesEndWithZ.size == listEndNodes.size) {
//                isFinish = true
//            }else{
//                tmpStartNodes = tmpEndNodes
//                instructionIndex++
//                if(instructionIndex > (instructionSize -1)){
//                    while(countTrick > 0) {
//                        countTrick--
//                        println(" ${tmpEndNodes.joinToString("-")} : ${nodesEndWithZ.size} ")
//                    }
//                    instructionIndex = 0
//                }
//            }
//        }


//        return steps
    }



//    val result1 = listOf("A","B","C") same listOf("B","A","C")
//    println("Result1: $result1")


    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("data_test")
//    part1(testInput).println()
//    check(part1(testInput) == 1)

//    val testInput2 = readInput("data_test_part2")
//    part2(testInput2).println()

    val input = readInput("data")
    //   part1(input).println()
    part2(input).println()
}
