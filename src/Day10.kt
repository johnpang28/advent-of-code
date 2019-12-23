package day10

import kotlin.math.sign
import kotlin.math.sqrt

fun main() {
    val input = """
        ##.#..#..###.####...######
        #..#####...###.###..#.###.
        ..#.#####....####.#.#...##
        .##..#.#....##..##.#.#....
        #.####...#.###..#.##.#..#.
        ..#..#.#######.####...#.##
        #...####.#...#.#####..#.#.
        .#..#.##.#....########..##
        ......##.####.#.##....####
        .##.#....#####.####.#.####
        ..#.#.#.#....#....##.#....
        ....#######..#.##.#.##.###
        ###.#######.#..#########..
        ###.#.#..#....#..#.##..##.
        #####.#..#.#..###.#.##.###
        .#####.#####....#..###...#
        ##.#.......###.##.#.##....
        ...#.#.#.###.#.#..##..####
        #....#####.##.###...####.#
        #.##.#.######.##..#####.##
        #.###.##..##.##.#.###..###
        #.####..######...#...#####
        #..#..########.#.#...#..##
        .##..#.####....#..#..#....
        .###.##..#####...###.#.#.#
        .##..######...###..#####.#
    """.trimIndent()

    val asteroids = input.toAsteroids()

    val lineOfSightCounts = asteroids.map { asteroid -> asteroid to directLineOfSight(asteroid, asteroids).size }
    val answer = lineOfSightCounts.maxBy { it.second }
    println(answer)
}

data class Asteroid(val x: Int, val y: Int)

data class Path(val x: Int, val y: Int) {
    fun distance(): Float = sqrt(x.toFloat() * x + y.toFloat() * y)
}

fun directLineOfSight(from: Asteroid, all: List<Asteroid>): List<Asteroid> {
    fun relativePath(to: Asteroid): Path = Path(to.x - from.x, to.y - from.y)

    fun closest(a1: Asteroid, a2: Asteroid): Asteroid =
        if (relativePath(a1).distance() > relativePath(a2).distance()) a2 else a1

    return all.fold(emptyList()) { acc, next ->
        val path = relativePath(next)
        if (path.distance() > 0f) {
            if (acc.isEmpty()) acc + next
            else acc.firstOrNull { isBlocking(path, relativePath(it)) }?.let { blocking ->
                val closest = closest(blocking, next)
                acc - blocking + closest
            } ?: acc + next
        } else acc
    }
}

fun isBlocking(p1: Path, p2: Path): Boolean =
    if (p1.x.sign == p2.x.sign && p1.y.sign == p2.y.sign) {
        if (p1.x == 0 || p1.y == 0) true
        else p1.x.toFloat() / p1.y.toFloat() == p2.x.toFloat() / p2.y.toFloat()
    } else false


fun String.toAsteroids(): List<Asteroid> {
    return this.lines().mapIndexed { y, row ->
        row.mapIndexed { x, pos ->
            if (pos == '#') Asteroid(x, y) else null
        }
    }.flatten().filterNotNull()
}
