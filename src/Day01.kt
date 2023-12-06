import java.lang.Exception

enum class TypeInput(val input: String) {
    SEED("seed"),
    SOIL("soil"),
    FERTILIZER("fertilizer"),
    WATER("water"),
    LIGHT("light"),
    TEMPERATURE("temperature"),
    HUMIDITY("humidity"),
    LOCATION("location");

    companion object {
        fun from(string: String): TypeInput =
            entries.associateBy { it.input }[string] ?: throw Exception("No type input for $string")
    }
}

data class DataBlock(
    val source: TypeInput,
    val destination: TypeInput,
    val sourceData: List<DataRow>,
    val destinationData: List<DataRow>
) {
    fun findDestinationFor(sourceInput: Long): Long {

        sourceData.forEach { dataRow ->
            val index = dataRow.indexInRange(sourceInput)
            if (index != -1L) {
                val rowPosition = dataRow.rowPosition
                val destinationData = destinationData.find { it.rowPosition == rowPosition }
                if (null != destinationData) {
                    val destination = destinationData.findValueFor(index)
                    return if (destination != -1L) destination else sourceInput
                }
            }
        }

        return sourceInput
    }

}

data class DataRow(val start: Long, val length: Long, val rowPosition: Int) {
    fun indexInRange(num: Long): Long {
        if (num < start) {
            return -1L
        }
        val lastNum = start + (length - 1L)
        if (num > lastNum) {
            return -1L
        }

        return (num - start)
//            .also {
//            println("Start: $start Length: $length Num: $num index: $it")
//        }

    }

    fun findValueFor(index: Long): Long {
        if (index >= length) {
            return -1L
        }
        return start + index
    }


}

fun main() {

    val list1 = listOf(1,2,3,4,5,6,7,8,9,10)
    val list2 = listOf(5,6,7,8,9,10,11,12,13,14,15)
    val list3 = listOf(12,13,14,15)
val result = list1 union list3
    result.forEach { print("$it, ") }

    fun List<String>.parse(): Pair<List<Long>, List<DataBlock>> {
        val listSeedId = mutableListOf<Long>()

        val listDataBlock = mutableListOf<DataBlock>()

        var tmpSourceTypeInput: TypeInput? = null
        var tmpDestinationTypeInput: TypeInput? = null
        val tmpListSourceDataRow = mutableListOf<DataRow>()
        val tmpListDestinationDataRow = mutableListOf<DataRow>()
        var tmpIndex = 0

        this.forEachIndexed { index, line ->

            if (line.contains("seeds")) {
                line.trim().split(":")[1].trim().split(" ").forEach {
                    listSeedId.add(it.toLong())
                }
            } else if (line.contains("map")) {
                val mapData = line.split(" ").first().trim().split("-")
                val source = mapData.first()
                val destination = mapData.last()
                tmpSourceTypeInput = TypeInput.from(source)
                tmpDestinationTypeInput = TypeInput.from(destination)
                tmpIndex = 0
                tmpListDestinationDataRow.clear()
                tmpListSourceDataRow.clear()
            } else if (line.isEmpty()) {

                if (tmpSourceTypeInput != null && tmpDestinationTypeInput != null) {
                    val dataBlock = DataBlock(
                        source = tmpSourceTypeInput!!,
                        destination = tmpDestinationTypeInput!!,
                        sourceData = tmpListSourceDataRow.toList(),
                        destinationData = tmpListDestinationDataRow.toList()
                    )
                    listDataBlock.add(dataBlock)
                }

            } else {
                val lineData = line.trim().split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
                if (lineData.size == 3) {
                    val destinationData =
                        DataRow(start = lineData.first(), length = lineData.last(), rowPosition = tmpIndex)
                    tmpListDestinationDataRow.add(destinationData)

                    val sourceData = DataRow(start = lineData[1], length = lineData.last(), rowPosition = tmpIndex)
                    tmpListSourceDataRow.add(sourceData)
                    tmpIndex++
                } else {
                    throw Exception("Invalid data")
                }

                if (index == (this.size - 1)) {
                    if (tmpSourceTypeInput != null && tmpDestinationTypeInput != null) {
                        val dataBlock = DataBlock(
                            source = tmpSourceTypeInput!!,
                            destination = tmpDestinationTypeInput!!,
                            sourceData = tmpListSourceDataRow,
                            destinationData = tmpListDestinationDataRow
                        )
                        listDataBlock.add(dataBlock)
                    }

                }
            }

        }

        return Pair(listSeedId.toList(), listDataBlock.toList())
    }


    fun findLocationForSeed(listSeedId: List<Long>, listDataBlock: List<DataBlock>): Map<Long, Long> {
        val hm = mutableMapOf<Long, Long>()
        val startInputType = TypeInput.SEED
        val endInputType = TypeInput.LOCATION
        listSeedId.forEach { seedId ->
            var tmpInputType = startInputType
            var lookingValue = seedId
            while (tmpInputType != endInputType) {
                val dataBlock = listDataBlock.first { it.source == tmpInputType }
                lookingValue = dataBlock.findDestinationFor(lookingValue)
                tmpInputType = dataBlock.destination
            }
            hm[seedId] = lookingValue
        }
        return hm.toMap()
    }

    fun part1(input: List<String>): Long {

        val (listSeedId, listDataBlock) = input.parse()

        val hm = findLocationForSeed(listSeedId, listDataBlock)

        // println(hm)

        return hm.values.min()
    }


    fun part2(input: List<String>): Long {

        val (listSeedId, listDataBlock) = input.parse()
//        val hm = mutableMapOf<Long, Long>()

//        val startListSeedId = listSeedId.chunked(2).map { it.first() to it[1] }.map { it.first }
//        val hm = findLocationForSeed(startListSeedId, listDataBlock)
        var minValue = -1L
        listSeedId.chunked(2).map { it.first() to it[1] }.forEach { p ->
            val start = p.first
            val length = p.second
            val last = start + length - 1L
            val list = (start..last).toList()
            val result = findLocationForSeed(list, listDataBlock)
            val tmpMin =  result.values.min()
            if(minValue == -1L || tmpMin < minValue){
                minValue = tmpMin
            }
          //  hm.putAll(result)
        }


        return minValue
    }

    // test if implementation meets criteria from the description, like:
  //  val testInput = readInput("data_test")
//    part1(testInput).println()

  //  part2(testInput).println()

    val input = readInput("data")
//    part1(input).println()
    part2(input).println()
}
