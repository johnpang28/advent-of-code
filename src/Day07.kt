package day07

import ComputerState
import asMemory
import doOps
import isAwaitingInput
import isCompleted

fun main() {
    val data = "3,8,1001,8,10,8,105,1,0,0,21,38,55,72,93,118,199,280,361,442,99999,3,9,1001,9,2,9,1002,9,5,9,101,4,9,9,4,9,99,3,9,1002,9,3,9,1001,9,5,9,1002,9,4,9,4,9,99,3,9,101,4,9,9,1002,9,3,9,1001,9,4,9,4,9,99,3,9,1002,9,4,9,1001,9,4,9,102,5,9,9,1001,9,4,9,4,9,99,3,9,101,3,9,9,1002,9,3,9,1001,9,3,9,102,5,9,9,101,4,9,9,4,9,99,3,9,101,1,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,99,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,101,1,9,9,4,9,99,3,9,101,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,99,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,99,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,99"

    val phases1 = listOf(0,1,2,3,4)
    val answer1 = permutations(phases1).mapNotNull { amplify(it, data) }.max()
    println(answer1)

    val phases2 = listOf(5,6,7,8,9)
    val answer2 = permutations(phases2).mapNotNull { amplify(it, data) }.max()
    println(answer2)

}

fun amplify(phases: List<Int>, data: String): Long? {

    fun initPhases(): List<ComputerState> = (0..4).map { i ->
        doOps(ComputerState(data.asMemory(), 0, phases[i].toLong(), emptyList()))
    }

    fun loop(inputToAmplifiers: Pair<Long, List<ComputerState>>): Pair<Long, List<ComputerState>> {
        val (input, amplifiers) = inputToAmplifiers
        return when {
            amplifiers.all { isCompleted(it) } -> inputToAmplifiers
            amplifiers.all { isAwaitingInput(it)} -> {
                loop(amplifiers.fold(Pair(input, emptyList())) { acc, next ->
                    val newState = doOps(next.copy(input = acc.first))
                    Pair(newState.output.last(), acc.second + newState)
                })
            }
            else -> inputToAmplifiers
        }
    }

    return if (phases.size == 5) {
        loop(0L to initPhases()).second.last().output.last()
    } else null
}

fun permutations(xs: List<Int>, acc: List<List<Int>> = emptyList()): List<List<Int>> {
    return when {
        xs.isEmpty() -> acc
        acc.isEmpty() -> xs.flatMap { x -> permutations(xs - x, listOf(listOf(x))) }
        else -> xs.flatMap { x  -> acc.flatMap { permutations(xs - x, listOf(it + x)) } }
    }
}