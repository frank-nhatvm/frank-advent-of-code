import java.lang.Exception

data class RowData(val source: Long, val destination: Long, val length: Long) {
    fun indexInRange(num: Long): Long {
        if (num < source) {
            return -1L
        }
        val index = num - source
        if (index > length) {
            return -1L
        }
        return index
    }

}

data class AlmanacData(
    val source: TypeInput,
    val destination: TypeInput,
    val rows: List<RowData>
) {
    private val hm = mutableMapOf<Long,Long>()
    fun findDestinationFor(source: Long): Long {

        rows.forEach { rowData ->
            val index = rowData.indexInRange(source)
            if (index != -1L) {
                return rowData.destination + index
            }
        }
        return source
    }

    fun getSavedLocationFor(source: Long) = hm[source]

    fun saved(location: Long, source: Long) {
        hm[source] = location
    }

}

fun main() {

    fun List<String>.toData(): Pair<List<Long>, List<AlmanacData>> {
        val listSeedId = mutableListOf<Long>()

        val listDataBlock = mutableListOf<AlmanacData>()

        var tmpSourceTypeInput: TypeInput? = null
        var tmpDestinationTypeInput: TypeInput? = null
        var tmpListRowData = mutableListOf<RowData>()

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
                tmpListRowData.clear()
            } else if (line.isEmpty()) {

                if (tmpSourceTypeInput != null) {
                    val dataBlock = AlmanacData(
                        source = tmpSourceTypeInput!!,
                        destination = tmpDestinationTypeInput!!,
                        rows = tmpListRowData.toList()
                    )
                    listDataBlock.add(dataBlock)
                }

            } else {
                val lineData = line.trim().split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
                if (lineData.size == 3) {
                    tmpListRowData.add(
                        RowData(
                            source = lineData[1],
                            destination = lineData.first(),
                            length = lineData.last()
                        )
                    )

                } else {
                    throw Exception("Invalid data")
                }

                if (index == (this.size - 1)) {
                    if (tmpSourceTypeInput != null) {
                        val dataBlock = AlmanacData(
                            source = tmpSourceTypeInput!!,
                            destination = tmpDestinationTypeInput!!,
                            rows = tmpListRowData.toList()
                        )
                        listDataBlock.add(dataBlock)
                    }

                }
            }

        }

        return Pair(listSeedId.toList(), listDataBlock.toList())
    }


//    fun part1(input: List<String>): Long {
//
//        val (listSeedId, listAlmanacData) = input.toData()
//
//        val hmSeedLocation = mutableMapOf<Long, Long>()
//        val hmSoilLocation = mutableMapOf<Long, Long>()
//        val hmFertilizerLocation = mutableMapOf<Long, Long>()
//        val hmWaterLocation = mutableMapOf<Long, Long>()
//        val hmLightLocation = mutableMapOf<Long, Long>()
//        val hmTemperatureLocation = mutableMapOf<Long, Long>()
//        val hmHumidityLocation = mutableMapOf<Long, Long>()
//
//
//        val seedSoil = listAlmanacData.first { it.source == TypeInput.SEED }
//        val soilFertilizer = listAlmanacData.first { it.source == TypeInput.SOIL }
//        val fertilizerWater = listAlmanacData.first { it.source == TypeInput.FERTILIZER }
//        val waterLight = listAlmanacData.first { it.source == TypeInput.WATER }
//        val lightTemperature = listAlmanacData.first { it.source == TypeInput.LIGHT }
//        val temperatureHumidity = listAlmanacData.first { it.source == TypeInput.TEMPERATURE }
//        val humidityLocation = listAlmanacData.first { it.source == TypeInput.HUMIDITY }
//
//
//        listSeedId.forEach { seedId ->
//
//            var soil = -1L
//            var fertilizer = -1L
//            var water = -1L
//            var light = -1L
//            var temperature = -1L
//            var humidity = -1L
//            var location = -1L
//
//
//            if (!hmSeedLocation.contains(seedId)) {
//                soil = seedSoil.findDestinationFor(seedId)
//                val savedSoilLocation = hmSoilLocation[soil]
//                if (savedSoilLocation != null) {
//                    location = savedSoilLocation
//                } else {
//                    fertilizer = soilFertilizer.findDestinationFor(soil)
//                    val savedFertilizerLocation = hmFertilizerLocation[fertilizer]
//                    if (savedFertilizerLocation != null) {
//                        location = savedFertilizerLocation
//
//                    } else {
//                        water = fertilizerWater.findDestinationFor(fertilizer)
//                        val savedWaterLocation = hmWaterLocation[water]
//                        if (savedWaterLocation != null) {
//                            location = savedWaterLocation
//                        } else {
//                            light = waterLight.findDestinationFor(water)
//                            val savedLightLocation = hmLightLocation[light]
//                            if (savedLightLocation != null) {
//                                location = savedLightLocation
//                            } else {
//                                temperature = lightTemperature.findDestinationFor(light)
//                                val savedTemperatureLocation = hmTemperatureLocation[temperature]
//                                if (savedTemperatureLocation != null) {
//                                    location = savedTemperatureLocation
//                                } else {
//                                    humidity = temperatureHumidity.findDestinationFor(temperature)
//                                    val savedHumidityLocation = hmHumidityLocation[humidity]
//                                    if (savedHumidityLocation != null) {
//                                        location = savedHumidityLocation
//                                    } else {
//                                        location = humidityLocation.findDestinationFor(humidity)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            hmSeedLocation[seedId] = location
//
//            if(location != -1L){
//                if(soil != -1L){
//                    hmSoilLocation[soil] = location
//                }
//                if(fertilizer!= -1L){
//                    hmFertilizerLocation[fertilizer] = location
//                }
//                if(water!= -1L){
//                    hmWaterLocation[water] = location
//                }
//                if(light!= -1L){
//                    hmLightLocation[light] = location
//                }
//                if(temperature!= -1L){
//                    hmTemperatureLocation[temperature] = location
//                }
//                if(humidity!= -1L){
//                    hmHumidityLocation[humidity] = location
//                }
//            }
//
//        }
//
//
//        return hmSeedLocation.values.min()
//    }

    fun part2(input: List<String>): Long {
        val (listSeedId, listAlmanacData) = input.toData()

        val hmSeedLocation = mutableMapOf<Long, Long>()
        val hmSoilLocation = mutableMapOf<Long, Long>()
        val hmFertilizerLocation = mutableMapOf<Long, Long>()
        val hmWaterLocation = mutableMapOf<Long, Long>()
        val hmLightLocation = mutableMapOf<Long, Long>()
        val hmTemperatureLocation = mutableMapOf<Long, Long>()
        val hmHumidityLocation = mutableMapOf<Long, Long>()


        val seedSoil = listAlmanacData.first { it.source == TypeInput.SEED }
        val soilFertilizer = listAlmanacData.first { it.source == TypeInput.SOIL }
        val fertilizerWater = listAlmanacData.first { it.source == TypeInput.FERTILIZER }
        val waterLight = listAlmanacData.first { it.source == TypeInput.WATER }
        val lightTemperature = listAlmanacData.first { it.source == TypeInput.LIGHT }
        val temperatureHumidity = listAlmanacData.first { it.source == TypeInput.TEMPERATURE }
        val humidityLocation = listAlmanacData.first { it.source == TypeInput.HUMIDITY }

        listSeedId.chunked(2).forEach {
            val start = it.first()
            val length = it[1]
            val end = start + length -1L
            val tmpListSeedId = (start..end)
            tmpListSeedId.forEach { seedId ->

                if (!hmSeedLocation.contains(seedId)) {
                    var fertilizer = -1L
                    var water = -1L
                    var light = -1L
                    var temperature = -1L
                    var humidity = -1L
                    val location: Long

                    var soil: Long = seedSoil.findDestinationFor(seedId)
                    val savedSoilLocation = hmSoilLocation[soil]
                    if (savedSoilLocation != null) {
                        location = savedSoilLocation
                    } else {
                        fertilizer = soilFertilizer.findDestinationFor(soil)
                        val savedFertilizerLocation = hmFertilizerLocation[fertilizer]
                        if (savedFertilizerLocation != null) {
                            location = savedFertilizerLocation

                        } else {
                            water = fertilizerWater.findDestinationFor(fertilizer)
                            val savedWaterLocation = hmWaterLocation[water]
                            if (savedWaterLocation != null) {
                                location = savedWaterLocation
                            } else {
                                light = waterLight.findDestinationFor(water)
                                val savedLightLocation = hmLightLocation[light]
                                if (savedLightLocation != null) {
                                    location = savedLightLocation
                                } else {
                                    temperature = lightTemperature.findDestinationFor(light)
                                    val savedTemperatureLocation = hmTemperatureLocation[temperature]
                                    if (savedTemperatureLocation != null) {
                                        location = savedTemperatureLocation
                                    } else {
                                        humidity = temperatureHumidity.findDestinationFor(temperature)
                                        val savedHumidityLocation = hmHumidityLocation[humidity]
                                        if (savedHumidityLocation != null) {
                                            location = savedHumidityLocation
                                        } else {
                                            location = humidityLocation.findDestinationFor(humidity)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    hmSeedLocation[seedId] = location

                    if(location != -1L){
                        if(soil != -1L){
                            hmSoilLocation[soil] = location
                        }
                        if(fertilizer!= -1L){
                            hmFertilizerLocation[fertilizer] = location
                        }
                        if(water!= -1L){
                            hmWaterLocation[water] = location
                        }
                        if(light!= -1L){
                            hmLightLocation[light] = location
                        }
                        if(temperature!= -1L){
                            hmTemperatureLocation[temperature] = location
                        }
                        if(humidity!= -1L){
                            hmHumidityLocation[humidity] = location
                        }
                    }
                }



            }
        }



        return hmSeedLocation.values.min()
    }

    // test if implementation meets criteria from the description, like:
      val testInput = readInput("data_test")
   // part1(testInput).println()

      part2(testInput).println()

     val input = readInput("data")
    //part1(input).println()
   //  part2(input).println()
}
