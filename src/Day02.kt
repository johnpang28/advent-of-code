fun main() {
    val input = "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,13,1,19,1,5,19,23,2,10,23,27,1,27,5,31,2,9,31,35,1,35,5,39,2,6,39,43,1,43,5,47,2,47,10,51,2,51,6,55,1,5,55,59,2,10,59,63,1,63,6,67,2,67,6,71,1,71,5,75,1,13,75,79,1,6,79,83,2,83,13,87,1,87,6,91,1,10,91,95,1,95,9,99,2,99,13,103,1,103,6,107,2,107,6,111,1,111,2,115,1,115,13,0,99,2,0,14,0"
    val initialState: Memory = input.split(",").mapIndexed { index, s -> index to s.toInt() }.toMap()
    val fixedInitialState: Memory = initialState.update(1 to 12).update(2 to 2)

    val finalState = doOps(0, fixedInitialState)

    println(finalState[0]) // 4714701
}

typealias Memory = Map<Int, Int>

data class Instruction(val opCode: Int, val parameters: List<Int>)

fun doOps(instructionPointer: Int, memory: Memory):Memory {

    fun nextInstruction(): Instruction? =
        when (val opCode: Int = memory.getValue(instructionPointer)) {
            1, 2 -> Instruction(opCode, (1..3).map { memory.getValue(instructionPointer + it) })
            99 -> Instruction(opCode, emptyList())
            else -> null
        }

    fun processInstruction(instruction: Instruction): Memory {

        fun binaryInstruction(f: (Int, Int) -> Int): Memory = with(instruction) {
            val result = f(memory.getValue(parameters[0]), memory.getValue(parameters[1]))
            memory.update(parameters[2] to result)
        }

        return when (instruction.opCode) {
            1 -> binaryInstruction(Int::plus)
            2 -> binaryInstruction(Int::times)
            else -> memory
        }
    }

    return nextInstruction()?.let { instruction ->
        if (instruction.opCode == 99) memory else {
            val updatedMemory = processInstruction(instruction)
            val newPointer = instructionPointer + instruction.parameters.size + 1
            doOps(newPointer, updatedMemory)
        }
    } ?: memory
}

fun Memory.update(kv: Pair<Int, Int>): Memory = (entries.filter { it.key != kv.first }.map { it.toPair() } + kv).toMap()