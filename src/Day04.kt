fun main() {
    val range = 130254..678275

    val answer1 = range.filter { satisfiesCriteria(it) }.size
    println(answer1)

    val answer2 = range.filter { satisfiesCriteria2(it) }.size
    println(answer2)
}

fun satisfiesCriteria(x: Int): Boolean = incrementCriteria(x) && adjacentCriteria(x)

fun satisfiesCriteria2(x: Int): Boolean = incrementCriteria(x) && doubles(x).isNotEmpty()

fun adjacentCriteria(x: Int): Boolean {
    val chars = x.toString().toCharArray()
    return (0..chars.size - 2).firstOrNull() { chars[it] == chars[it + 1] } != null
}

fun incrementCriteria(x: Int): Boolean {
    val ints = x.toString().map { it.toInt() }
    return (0..ints.size - 2).all { ints[it] <= ints[it + 1] }
}

fun doubles(x: Int): List<String> {
    val chars = x.toString().toCharArray()
    val multiples: List<String> = chars.fold(emptyList()) { acc, c ->
        when {
            acc.isEmpty() -> listOf(c.toString())
            acc.last().last() == c -> acc.take(acc.size - 1) + (acc.last() + c)
            else -> acc + c.toString()
        }
    }
    return multiples.filter { it.length == 2}
}