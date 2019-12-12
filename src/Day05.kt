package day05

import day05.Mode.Immediate
import day05.Mode.Position

fun main() {
    val inputData = "3,225,1,225,6,6,1100,1,238,225,104,0,1101,11,91,225,1002,121,77,224,101,-6314,224,224,4,224,1002,223,8,223,1001,224,3,224,1,223,224,223,1102,74,62,225,1102,82,7,224,1001,224,-574,224,4,224,102,8,223,223,1001,224,3,224,1,224,223,223,1101,28,67,225,1102,42,15,225,2,196,96,224,101,-4446,224,224,4,224,102,8,223,223,101,6,224,224,1,223,224,223,1101,86,57,225,1,148,69,224,1001,224,-77,224,4,224,102,8,223,223,1001,224,2,224,1,223,224,223,1101,82,83,225,101,87,14,224,1001,224,-178,224,4,224,1002,223,8,223,101,7,224,224,1,223,224,223,1101,38,35,225,102,31,65,224,1001,224,-868,224,4,224,1002,223,8,223,1001,224,5,224,1,223,224,223,1101,57,27,224,1001,224,-84,224,4,224,102,8,223,223,1001,224,7,224,1,223,224,223,1101,61,78,225,1001,40,27,224,101,-89,224,224,4,224,1002,223,8,223,1001,224,1,224,1,224,223,223,4,223,99,0,0,0,677,0,0,0,0,0,0,0,0,0,0,0,1105,0,99999,1105,227,247,1105,1,99999,1005,227,99999,1005,0,256,1105,1,99999,1106,227,99999,1106,0,265,1105,1,99999,1006,0,99999,1006,227,274,1105,1,99999,1105,1,280,1105,1,99999,1,225,225,225,1101,294,0,0,105,1,0,1105,1,99999,1106,0,300,1105,1,99999,1,225,225,225,1101,314,0,0,106,0,0,1105,1,99999,1008,677,226,224,1002,223,2,223,1006,224,329,101,1,223,223,8,226,677,224,102,2,223,223,1005,224,344,101,1,223,223,1107,226,677,224,102,2,223,223,1006,224,359,101,1,223,223,1007,226,226,224,102,2,223,223,1006,224,374,101,1,223,223,7,677,677,224,102,2,223,223,1005,224,389,1001,223,1,223,108,677,677,224,1002,223,2,223,1005,224,404,101,1,223,223,1008,226,226,224,102,2,223,223,1005,224,419,1001,223,1,223,1107,677,226,224,102,2,223,223,1005,224,434,1001,223,1,223,1108,677,677,224,102,2,223,223,1006,224,449,1001,223,1,223,7,226,677,224,102,2,223,223,1005,224,464,101,1,223,223,1008,677,677,224,102,2,223,223,1005,224,479,101,1,223,223,1007,226,677,224,1002,223,2,223,1006,224,494,101,1,223,223,8,677,226,224,1002,223,2,223,1005,224,509,101,1,223,223,1007,677,677,224,1002,223,2,223,1006,224,524,101,1,223,223,107,226,226,224,102,2,223,223,1006,224,539,101,1,223,223,107,226,677,224,102,2,223,223,1005,224,554,1001,223,1,223,7,677,226,224,102,2,223,223,1006,224,569,1001,223,1,223,107,677,677,224,1002,223,2,223,1005,224,584,101,1,223,223,1107,677,677,224,102,2,223,223,1005,224,599,101,1,223,223,1108,226,677,224,102,2,223,223,1006,224,614,101,1,223,223,8,226,226,224,102,2,223,223,1006,224,629,101,1,223,223,108,226,677,224,102,2,223,223,1005,224,644,1001,223,1,223,108,226,226,224,102,2,223,223,1005,224,659,101,1,223,223,1108,677,226,224,102,2,223,223,1006,224,674,1001,223,1,223,4,223,99,226"
    val initialMemory: Memory = inputData.split(",").mapIndexed { index, s -> index to s.toInt() }.toMap()

    val answer1 = doOps(0, initialMemory, 1, emptyList()).output
    println(answer1)

    val answer2 = doOps(0, initialMemory, 5, emptyList()).output
    println(answer2)
}

typealias Memory = Map<Int, Int>
fun Memory.update(kv: Pair<Int, Int>): Memory = (entries.filter { it.key != kv.first }.map { it.toPair() } + kv).toMap()
fun Memory.address(address: Int): Int = getValue(address)

data class Instruction(val operation: Operation, val parameters: List<Int>)

enum class Mode { Position, Immediate }

data class Operation(val opCode: Int, val paramModes: List<Mode>)

val opCodeRegex = Regex("""^[01]{0,3}[0-9]{1,2}$""")

fun Boolean.toInt() = if (this) 1 else 0

fun parseOp(x: Int): Operation? {
    val opString = x.toString().padStart(5, '0')
    return if (opCodeRegex.matches(opString)) {
        Operation(
            opCode = opString.takeLast(2).toInt(),
            paramModes = listOf(
                if (opString[2] == '0') Position else Immediate,
                if (opString[1] == '0') Position else Immediate,
                if (opString[0] == '0') Position else Immediate)
        )
    } else null
}

data class Result(val memory: Memory, val output: List<Int>, val newPointer: Int? = null)

fun doOps(instructionPointer: Int, memory: Memory, input: Int, accumulatedOutput: List<Int>): Result {

    fun nextInstruction(): Instruction? {
        return parseOp(memory.address(instructionPointer))?.let { op ->
            when (op.opCode) {
                1, 2, 7, 8 -> Instruction(op, (1..3).map { memory.address(instructionPointer + it) })
                3, 4 -> Instruction(op, listOf(memory.address(instructionPointer + 1)))
                5, 6 -> Instruction(op, (1..2).map { memory.address(instructionPointer + it) })
                99 -> Instruction(op, emptyList())
                else -> null
            }
        }
    }

    fun processInstruction(instruction: Instruction): Result {

        fun read(paramIndex: Int): Int = with(instruction) {
            when (operation.paramModes[paramIndex]) {
                Position -> memory.address(parameters[paramIndex])
                Immediate -> parameters[paramIndex]
            }
        }

        fun binaryInstruction(f: (Int, Int) -> Int): Memory = with(instruction) {
            val result = f(read(0), read(1))
            memory.update(parameters[2] to result)
        }

        return when (instruction.operation.opCode) {
            1 -> Result(binaryInstruction(Int::plus), accumulatedOutput)
            2 -> Result(binaryInstruction(Int::times), accumulatedOutput)
            3 -> Result(memory.update(instruction.parameters[0] to input), accumulatedOutput)
            4 -> Result(memory, accumulatedOutput + read(0))
            5 -> {
                if (read(0) != 0) Result(memory, accumulatedOutput, read(1))
                else Result(memory, accumulatedOutput)
            }
            6 -> {
                if (read(0) == 0) Result(memory, accumulatedOutput, read(1))
                else Result(memory, accumulatedOutput)
            }
            7 -> Result(memory.update(instruction.parameters[2] to (read(0) < read(1)).toInt()), accumulatedOutput)
            8 -> Result(memory.update(instruction.parameters[2] to (read(0) == read(1)).toInt()), accumulatedOutput)
            else -> Result(memory, accumulatedOutput)
        }

    }

    return nextInstruction()?.let { instruction ->
        if (instruction.operation.opCode == 99) Result(memory, accumulatedOutput)
        else {
            val instructionResult = processInstruction(instruction)
            val newPointer = instructionResult.newPointer ?: instructionPointer + instruction.parameters.size + 1
            doOps(newPointer, instructionResult.memory, input, instructionResult.output)
        }
    } ?: Result(memory, accumulatedOutput)
}
