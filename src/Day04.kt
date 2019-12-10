fun main() {
    val range = 130254..678275
    val valid = range.filter { satisfiesCriteria(it) }
    val answer = valid.size
    println(answer)
}

fun satisfiesCriteria(x: Int): Boolean = incrementCriteria(x) && adjacentCriteria(x)

fun adjacentCriteria(x: Int): Boolean {
    val chars = x.toString().toCharArray()
    return (0..chars.size - 2).firstOrNull() { chars[it] == chars[it + 1] } != null
}

fun incrementCriteria(x: Int): Boolean {
    val ints = x.toString().map { it.toInt() }
    return (0..ints.size - 2).all { ints[it] <= ints[it + 1] }
}