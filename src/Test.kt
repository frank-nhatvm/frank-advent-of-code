
fun main() {

   val list = listOf(1,2,3,4,5,6)
    list.forEach {
        if(it >= 3){
            return@forEach
        }
        println("$it")
    }
}