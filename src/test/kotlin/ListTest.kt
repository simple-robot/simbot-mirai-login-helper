fun main() {
    val list = listOf(1,2,3,4)
    for (i in list.indices) {
        for (j in (i + 1) until list.size) {
            println("${list[i]}: ${list[j]}")
        }
    }
}