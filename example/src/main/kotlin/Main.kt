import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    simpleExample()
    println("------------")
    ktorExample()
    println("------------")
    retrofitExample()
}
