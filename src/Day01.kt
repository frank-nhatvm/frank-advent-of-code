import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder
import kotlin.math.abs

data class Node(val row: Int, val colum: Int, val isStar: Boolean, val starIndex: Int = 0)

fun main() {


    fun expandRowNodeFor(colum: Int, nodes: List<Node>): List<Node> {
        val newNodes = mutableListOf<Node>()
        nodes.forEachIndexed { c, node ->
            if (c < colum) {
                newNodes.add(node)
            } else if (c == colum) {
                newNodes.add(node)
                newNodes.add(node.copy(colum = node.colum + 1))
            } else {
                newNodes.add(node.copy(colum = node.colum + 1))
            }
        }
        return newNodes.toList()
    }

    fun expandAllNodeFor(colum: Int, nodes: List<Node>): List<Node> {
        val newNodes = mutableListOf<Node>()
        val nodesByRow = nodes.groupBy { it.row }
        nodesByRow.keys.forEach { row ->
            nodesByRow[row]?.let { rowNodes ->
                val expandedRowNodes = expandRowNodeFor(colum, rowNodes)
                newNodes.addAll(expandedRowNodes)
            }
        }
        return newNodes.toList()
    }


    fun List<String>.toOriginalData(): List<Node> {
        val nodes = mutableListOf<Node>()
        var countStar = 1
        var row = 0
        forEach { line ->
            val allNodesInLine = mutableListOf<Node>()
            line.forEachIndexed { colum, c ->
                val isStar = c == '#'
                val node = Node(row = row, colum = colum, isStar = isStar, starIndex = if (isStar) countStar else 0)
                nodes.add(node)
                allNodesInLine.add(node)
                if (isStar) {
                    countStar++
                }
            }

            if (line.toSet().size == 1) {
                // trick to check line just contains  dot (.) character
                row++
                allNodesInLine.forEach {
                    nodes.add(it.copy(row = row))
                }
            }
            row++
        }

        val nodesByColumn = nodes.groupBy { it.colum }

        val allColumnIndexWithoutStar = nodesByColumn.keys.filter { column ->
            (nodesByColumn[column]?.filter { it.isStar }?.size == 0)
        }.sorted()


        var expandedNodes = nodes.toList()
        var countColumn = 0
        allColumnIndexWithoutStar.forEach { column ->
            expandedNodes = expandAllNodeFor(column + countColumn, expandedNodes.toList())
            countColumn++
        }


        return expandedNodes.toList()
    }

    fun weightBetween(start: Node, node2: Node): Int {
        val startRow = start.row
        val startColumn = start.colum
        val row = node2.row
        val column = node2.colum
        return if (row == startRow) {
            if (column == startColumn + 1 || column == startColumn - 1) {
                1
            } else {
                Int.MAX_VALUE
            }
        } else if (column == startColumn) {
            if (row == startRow - 1 || row == startRow + 1) {
                1
            } else {
                Int.MAX_VALUE
            }
        } else {
            Int.MAX_VALUE
        }
    }


    fun dijstra(start: Node, end: Node, allNodes: List<Node>) {

        val distance = mutableMapOf<Node, Int>()
        val previousHm = mutableMapOf<Node, Node>()
        val final = mutableMapOf<Node, Boolean>()


        allNodes.forEach { currentNode ->
            final[currentNode] = false
            previousHm[currentNode] = start

            distance[currentNode] = weightBetween(start, currentNode)

            // startColumn -1, row

            // startColumn + 1,
        }

        previousHm[start] = start
        distance[start] = 0
        final[start] = true
        var minp = Int.MAX_VALUE
        var u: Node? = null
        val listTempNodes = mutableListOf<Node>()
        while (final[end] != true) {
            allNodes.forEach { currentNode ->
                if (final[currentNode] != true && minp > distance.getOrDefault(currentNode, Int.MAX_VALUE)) {
                    u = currentNode
                    minp = distance.getOrDefault(currentNode, Int.MAX_VALUE)
                }
            }

            u?.let { tempNode ->
                final[tempNode] = true
                if (final[end] != true) {

                    allNodes.forEach { currentNode ->
                        if (final[currentNode] != true && distance.getOrDefault(
                                tempNode,
                                Int.MAX_VALUE
                            ) + weightBetween(tempNode, currentNode) < distance.getOrDefault(
                                currentNode,
                                Int.MAX_VALUE
                            )
                        ) {

                            distance[currentNode] = distance.getOrDefault(
                                tempNode,
                                Int.MAX_VALUE
                            ) + weightBetween(tempNode, currentNode)
                            previousHm[currentNode] = tempNode

                        }
                    }

                }
            } ?: {
                throw Exception("Can not temp node")
            }
        }

        distance.println()
        previousHm.println()
    }

    fun findNextNodes(currentNode: Node, allNodes: List<Node>): List<Node> {
        val nextNodes = mutableListOf<Node>()
        val startRow = currentNode.row
        val startColumn = currentNode.colum

        allNodes.forEach { node ->
            val row = node.row
            val column = node.colum
            if (row == startRow) {
                if (column == startColumn + 1 || column == startColumn - 1) {
                    nextNodes.add(node)
                }
            } else if (column == startColumn) {
                if (row == startRow + 1 || row == startRow - 1) {
                    nextNodes.add(node)
                }
            }
        }
        return nextNodes.toList()

    }

    fun findShortestDistance(start: Node, end: Node, allNodes: List<Node>): Int {

        return     abs(start.colum - end.colum) + abs(start.row - end.row)

//        if (start.row == end.row) {
//            abs(start.colum - end.colum)
//        }
//        if (start.colum == end.colum) {
//            abs(start.row - end.row)
//        }
//
//        val (startRow, endRow) = listOf(start.row, end.row).sorted()
//        val (startColumn, endColumn) = listOf(start.colum, end.colum).sorted()
//        val nodesToCheck =
//            allNodes.filter { it.row in (startRow..endRow) && it.colum in (startColumn..endColumn) }.toMutableList()
//        val hmDistance = mutableMapOf<Node, Int>()
//        val hmPrevious = mutableMapOf<Node, Node?>()
//        val currentCheckingNodes = mutableListOf(start)
//
//        while (currentCheckingNodes.isNotEmpty()) {
//            val checkingNode = currentCheckingNodes.first()
//            currentCheckingNodes.remove(checkingNode)
//
//            val nextCheckingNodes = findNextNodes(checkingNode, nodesToCheck)
//            nextCheckingNodes.forEach { nextCheckingNode ->
//                val currentDistance = hmDistance[nextCheckingNode]
//                val previousDistance = hmDistance.getOrDefault(checkingNode, 0)
//                if (currentDistance == null || currentDistance > previousDistance + 1) {
//                    hmDistance[nextCheckingNode] = previousDistance + 1
//                    hmPrevious[nextCheckingNode] = checkingNode
//                    currentCheckingNodes.add(nextCheckingNode)
//                }
//            }
//
//        }
//
//        return hmDistance[end] ?: 0

    }

    fun allPairWithFirst(nodes: List<Node>): List<Pair<Node, Node>> {
        val allPairNodes = mutableListOf<Pair<Node, Node>>()

        val iterator = nodes.iterator()
        if (!iterator.hasNext()) return emptyList()

        val first = iterator.next()
        while (iterator.hasNext()) {
            val next = iterator.next()
            allPairNodes.add(Pair(first, next))
        }

        return allPairNodes.toList()
    }

    fun part1(input: List<String>): Int = runBlocking {

        val nodes = input.toOriginalData().sortedWith(compareBy({ it.row }, { it.colum }))

//        val nodesByRow = nodes.groupBy { it.row }
//        nodes.groupBy { it.colum }.keys.joinToString("  ").println()
//
//        nodesByRow.keys.forEachIndexed { index, row ->
//            val builder = StringBuilder()
//            builder.append("$index  ")
//            nodesByRow[row]?.let { listNodes ->
//                listNodes.forEach { node ->
//                    val value = if (node.isStar) "${node.starIndex}" else "."
//                    builder.append("$value  ")
//                }
//            }
//            println(builder.toString())
//        }

        val allStarNodes = nodes.filter { it.isStar }.toMutableList()
        val allPairNodes = mutableListOf<Pair<Node, Node>>()

        while (allStarNodes.isNotEmpty()) {
            val pairs = allPairWithFirst(allStarNodes)
            allPairNodes.addAll(pairs)
            allStarNodes.removeAt(0)
        }

        return@runBlocking allPairNodes.chunked(100).map { list ->
            async {
                list.map { (start, end) ->
                    findShortestDistance(start = start, end = end, allNodes = nodes)
                }.sum()
            }
        }.awaitAll().sum()

//        return@runBlocking allPairNodes.map { (start, end) ->
//            async { findShortestDistance(start = start, end = end, allNodes = nodes) }
//        }.awaitAll().sum()


//        return@runBlocking allPairNodes.sumOf { (start, end) ->
//            findShortestDistance(start = start, end = end, allNodes = nodes)
//                .also {
//                    println("${start.starIndex}-${end.starIndex}: $it")
//                }
//        }


//        val startOne = nodes.first { it.starIndex == 4 }
//        val startNine = nodes.first { it.starIndex == 5 }
//
//        findShortestDistance(start = startOne, end = startNine, allNodes = nodes)
//
//        // nodes.println()
//        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("data_test")
     part1(testInput).println()

    val input = readInput("data")
    part1(input).println()
//    part2(input).println()
}
