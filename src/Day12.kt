package day12

import kotlin.math.absoluteValue

fun main() {
    val m1 = Moon(Position(1, -4, 3))
    val m2 = Moon(Position(-14, 9, -4))
    val m3 = Moon(Position(-4, -6, 7))
    val m4 = Moon(Position(6, -9, -11))

    val moons = steps(listOf(m1, m2, m3, m4), 1000)
    val answer = moons.map { it.totalEnergy() }.sum()
    println(answer)
}

data class Position(val x: Int, val y: Int, val z: Int)
data class Velocity(val x: Int, val y: Int, val z: Int)
data class Moon(val position: Position, val velocity: Velocity = Velocity(0, 0, 0)) {
    fun kineticEnergy(): Int = velocity.x.absoluteValue + velocity.y.absoluteValue + velocity.z.absoluteValue
    fun potentialEnergy(): Int = position.x.absoluteValue + position.y.absoluteValue + position.z.absoluteValue
    fun totalEnergy(): Int = kineticEnergy() * potentialEnergy()
}

fun applyGravity(a: Moon, b: Moon): Pair<Velocity, Velocity> {

    fun velocityChange(p1: Int, p2: Int): Pair<Int, Int> = when {
        p1 > p2 -> Pair(-1, 1)
        p1 < p2 -> Pair(1, -1)
        else -> Pair(0, 0)
    }

    val (ax, bx) = velocityChange(a.position.x, b.position.x)
    val (ay, by) = velocityChange(a.position.y, b.position.y)
    val (az, bz) = velocityChange(a.position.z, b.position.z)

    return Pair(Velocity(ax, ay, az), Velocity(bx, by, bz))
}

fun applyVelocity(moon: Moon): Moon = moon.copy(position = Position(
    x = moon.position.x + moon.velocity.x,
    y = moon.position.y + moon.velocity.y,
    z = moon.position.z + moon.velocity.z
))

fun permutations(moons: List<Moon>): List<Pair<Moon, Moon>> {

    fun loop(l: List<Moon>, acc: List<Pair<Moon, Moon>>): List<Pair<Moon, Moon>> {
        return if (l.size == 1) acc
        else {
            val tail = l.drop(1)
            loop(tail, acc + tail.map { l.first() to it })
        }
    }

    return loop(moons, emptyList())
}

fun step(moons: List<Moon>): List<Moon> {
    val velocityMap = mutableMapOf(*moons.map { it to emptyList<Velocity>() }.toTypedArray())
    val perms = permutations(moons)
    perms.forEach { (m1, m2) ->
        val (v1, v2) = applyGravity(m1, m2)
        velocityMap[m1] = velocityMap.getValue(m1) + v1
        velocityMap[m2] = velocityMap.getValue(m2) + v2
    }
    return moons.map { m ->
        val velocityChange = velocityMap.getValue(m).reduce { v1, v2 -> Velocity(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z) }
        Moon(m.position, Velocity(m.velocity.x + velocityChange.x, m.velocity.y + velocityChange.y, m.velocity.z + velocityChange.z))
    }.map { applyVelocity(it) }
}

fun steps(moons: List<Moon>, stepCount: Int): List<Moon> {
    return if (stepCount == 0) moons
    else steps(step(moons), stepCount - 1)
}