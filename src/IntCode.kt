import Mode.Immediate
import Mode.Position

typealias Memory = Map<Int, Int>
fun Memory.update(kv: Pair<Int, Int>): Memory = (entries.filter { it.key != kv.first }.map { it.toPair() } + kv).toMap()
fun Memory.address(address: Int): Int = getValue(address)
fun String.asMemory(): Memory = split(",").mapIndexed { index, s -> index to s.toInt() }.toMap()

data class Instruction(val operation: Operation, val parameters: List<Int>)

enum class Mode { Position, Immediate }

data class Operation(val opCode: Int, val paramModes: List<Mode>)

data class ComputerState(val memory: Memory, val pointer: Int, val input: Int?, val output: List<Int>)

val opCodeRegex = Regex("""^[01]{0,3}[0-9]{1,2}$""")

fun isAwaitingInput(state: ComputerState): Boolean = opCode(state) == 3

fun isCompleted(state: ComputerState): Boolean = opCode(state) == 99

private fun opCode(state: ComputerState) = nextInstruction(state)?.operation?.opCode


fun nextInstruction(state: ComputerState): Instruction? {
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

    return with(state) {
        parseOp(memory.address(pointer))?.let { op ->
            when (op.opCode) {
                1, 2, 7, 8 -> Instruction(op, (1..3).map { memory.address(pointer + it) })
                3, 4 -> Instruction(op, kotlin.collections.listOf(memory.address(pointer + 1)))
                5, 6 -> Instruction(op, (1..2).map { memory.address(pointer + it) })
                99 -> Instruction(op, kotlin.collections.emptyList())
                else -> null
            }
        }
    }
}

fun doOps(state: ComputerState): ComputerState {

    fun processInstruction(instruction: Instruction): ComputerState {

        fun read(paramIndex: Int): Int = with(instruction) {
            when (operation.paramModes[paramIndex]) {
                Position -> state.memory.address(parameters[paramIndex])
                Immediate -> parameters[paramIndex]
            }
        }

        fun binaryInstruction(f: (Int, Int) -> Int): Memory = with(instruction) {
            val result = f(read(0), read(1))
            state.memory.update(parameters[2] to result)
        }

        fun Boolean.toInt() = if (this) 1 else 0

        fun movePointer(): Int = state.pointer + instruction.parameters.size + 1

        return when (instruction.operation.opCode) {
            1 -> state.copy(memory = binaryInstruction(Int::plus), pointer = movePointer())
            2 -> state.copy(memory = binaryInstruction(Int::times), pointer = movePointer())
            3 -> state.input?.let {
                state.copy(memory = state.memory.update(instruction.parameters[0] to it), pointer = movePointer(), input = null)
            } ?: state
            4 -> state.copy(pointer = movePointer(), output = state.output + read(0))
            5 -> {
                if (read(0) != 0) state.copy(pointer = read(1))
                else state.copy(pointer = movePointer())
            }
            6 -> {
                if (read(0) == 0) state.copy(pointer = read(1))
                else state.copy(pointer = movePointer())
            }
            7 -> state.copy(memory = state.memory.update(instruction.parameters[2] to (read(0) < read(1)).toInt()), pointer = movePointer())
            8 -> state.copy(memory = state.memory.update(instruction.parameters[2] to (read(0) == read(1)).toInt()), pointer = movePointer())
            else -> state.copy(pointer = movePointer())
        }

    }

    return nextInstruction(state)?.let { instruction ->
        if (instruction.operation.opCode == 99 || (instruction.operation.opCode == 3 && state.input == null)) state
        else {
            val newState = processInstruction(instruction)
            doOps(newState)
        }
    } ?: state
}