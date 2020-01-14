/**
 * https://adventofcode.com/2019/day/10
 */
package day10

import java.lang.Math.PI
import kotlin.math.atan2
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
    val (monitoringStation, numberOfAsteroidsSeen) = lineOfSightCounts.maxBy { it.second }!!
    println(numberOfAsteroidsSeen)

    val targets = vaporisationOrder(monitoringStation, asteroids)
    val asteroid200 = targets[199]
    val answer2 = asteroid200.x * 100 + asteroid200.y
    println(answer2)
}

data class Asteroid(val x: Int, val y: Int)

data class Path(val x: Int, val y: Int) {

    fun distance(): Float = sqrt(x.toFloat() * x + y.toFloat() * y)

    fun angle(): Double? =
        if (distance() == 0f) null
        else ((atan2(y.toDouble(), x.toDouble()) * 180 / PI) + 360 + 90) % 360
}

fun Asteroid.relativeTo(to: Asteroid): Path = Path(to.x - this.x, to.y - this.y)

fun directLineOfSight(from: Asteroid, all: List<Asteroid>): List<Asteroid> {

    fun closest(a1: Asteroid, a2: Asteroid): Asteroid =
        if (from.relativeTo(a1).distance() > from.relativeTo(a2).distance()) a2 else a1

    return all.fold(emptyList()) { acc, next ->
        val path = from.relativeTo(next)
        if (path.distance() > 0f) {
            if (acc.isEmpty()) acc + next
            else acc.firstOrNull { isBlocking(path, from.relativeTo(it)) }?.let { blocking ->
                val closest = closest(blocking, next)
                acc - blocking + closest
            } ?: acc + next
        } else acc
    }
}

fun vaporisationOrder(from: Asteroid, all: List<Asteroid>): List<Asteroid> {
    fun loop(remaining: List<Asteroid>,  vaporisedAcc: List<Asteroid>): List<Asteroid> {
        val vaporised = directLineOfSight(from, remaining)
            .map { asteroid -> asteroid to from.relativeTo(asteroid).angle() }
            .sortedBy { (_, angle) -> angle }
            .map { (asteroid, _) -> asteroid }
        val newRemaining = remaining - vaporised
        return if (newRemaining.isEmpty()) vaporisedAcc + vaporised else loop(newRemaining, vaporisedAcc + vaporised)
    }
    return loop(all - from, emptyList())
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
