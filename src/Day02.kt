import java.lang.Exception
import java.lang.StringBuilder

enum class CubeType(val color: String) {
    BLUE("blue"),
    RED("red"),
    GREEN("green"),
    ;

    companion object {
        fun from(value: String) = values().associateBy { it.color }[value] ?: throw Exception("Unknown  cube type")
    }
}

data class Cube(val cubeType: CubeType, val count: Int) {
    companion object {
        // eg: 2 green
        fun from(string: String): Cube {
            val list = string.split(" ")
            if (list.size != 2) {
                throw Exception("Invalid data for cube")
            }
            val count = list[0].trim().toInt()
            val cubeType = CubeType.from(list[1].trim())
            return Cube(cubeType, count)
        }
    }

    override fun toString(): String {
        return "Cube: ${cubeType.color}-$count"
    }

}

data class GameSet( val cubes: List<Cube>){
    // only 12 red cubes, 13 green cubes, and 14 blue cubes
    fun validPart1(): Boolean {
        val countOfRed = countByCubeType(CubeType.RED)
        if (countOfRed > 12) {
            return false
        }
        val countOfGreen = countByCubeType(CubeType.GREEN)
        if (countOfGreen > 13) {
            return false
        }
        val countOfBlue = countByCubeType(CubeType.BLUE)
        if (countOfBlue > 14) {
            return false
        }
        return true
    }

    fun countByCubeType(cubeType: CubeType) = cubes.filter { it.cubeType == cubeType }.sumOf { it.count }

    companion object{
        fun from(setGame: String): GameSet{
            val cubes = setGame.split(", ").map { cubeData -> Cube.from(cubeData) }
            return GameSet(cubes)
        }
    }
}

data class Game(val gameId: Int, val listGameSets: List<GameSet>) {

    fun power(): Int{
        val cubes = listGameSets.flatMap { it.cubes }
        val maxCountOfRedCube = cubes.filter { it.cubeType == CubeType.RED }.maxOf { it.count }
        val maxCountOfBlueCube = cubes.filter { it.cubeType == CubeType.BLUE }.maxOf { it.count }
        val maxCountOfGreenCube = cubes.filter { it.cubeType == CubeType.GREEN }.maxOf { it.count }
        return (maxCountOfBlueCube * maxCountOfGreenCube * maxCountOfRedCube)
    }

    fun validPart1():Boolean{
        listGameSets.forEach {
            if(!it.validPart1()){
                return false
            }
        }
        return true
    }
    companion object {
        fun from(string: String): Game {
            val list = string.trim().split(": ")
            if (list.size != 2) {
                throw Exception("invalid data for game")
            }
            val gameId = list[0].trim().split(" ")[1].trim().toInt()

            val listSetGames = list[1].trim().split("; ")
            val listGameSets = listSetGames.map { GameSet.from(it) }

            return Game(gameId, listGameSets)
        }
    }


}

fun main() {

    fun listGameFromInput(input:List<String>) = input.map {line -> Game.from(line)}

    fun part1(input: List<String>): Int {
        val games = listGameFromInput(input).filter { it.validPart1() }
        return games.sumOf { it.gameId }
    }

    fun part2(input: List<String>): Int {
        return  listGameFromInput(input).sumOf { it.power() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    val input = readInput("Day02")


//    println(part1(testInput))
    part1(input).println()

//    println(part2(testInput))
    part2(input).println()
}
