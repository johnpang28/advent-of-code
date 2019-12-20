import Mode.Immediate
import Mode.Position
import Mode.Relative
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

typealias Memory = Map<Long, BigInteger>
fun Memory.update(kv: Pair<Long, BigInteger>): Memory = (entries.filter { it.key != kv.first }.map { it.toPair() } + kv).toMap()
fun Memory.address(address: Long): BigInteger = getOrDefault(address, ZERO)
fun String.asMemory(): Memory = split(",").mapIndexed { index, s -> index.toLong() to s.toBigInteger() }.toMap()

data class Instruction(val operation: Operation, val parameters: List<BigInteger>)

enum class Mode { Position, Immediate, Relative }

data class Operation(val opCode: Int, val paramModes: List<Mode>)

data class ComputerState(val memory: Memory, val pointer: Long, val input: Long?, val output: List<BigInteger>, val relativeBase: Long = 0)

val opCodeRegex = Regex("""^[012]{0,3}[0-9]{1,2}$""")

fun isAwaitingInput(state: ComputerState): Boolean = opCode(state) == 3

fun isCompleted(state: ComputerState): Boolean = opCode(state) == 99

private fun opCode(state: ComputerState) = nextInstruction(state)?.operation?.opCode

fun nextInstruction(state: ComputerState): Instruction? {
    fun parseOp(x: BigInteger): Operation? {
        fun Char.toMode() = when (this) {
            '0' -> Position
            '1' -> Immediate
            '2' -> Relative
            else -> throw Exception("Unexpected mode: $this")
        }

        val opString = x.toString().padStart(5, '0')
        return if (opCodeRegex.matches(opString)) {
            Operation(
                opCode = opString.takeLast(2).toInt(),
                paramModes = listOf(
                    opString[2].toMode(),
                    opString[1].toMode(),
                    opString[0].toMode())
            )
        } else null
    }

    return with(state) {
        parseOp(memory.address(pointer))?.let { op ->
            when (op.opCode) {
                1, 2, 7, 8 -> Instruction(op, (1..3).map { memory.address(pointer + it) })
                3, 4, 9 -> Instruction(op, listOf(memory.address(pointer + 1)))
                5, 6 -> Instruction(op, (1..2).map { memory.address(pointer + it) })
                99 -> Instruction(op, emptyList())
                else -> null
            }
        }
    }
}

tailrec fun doOps(state: ComputerState): ComputerState {

    fun processInstruction(instruction: Instruction): ComputerState {

        fun movePointer(): Long = state.pointer + instruction.parameters.size + 1

        fun read(paramIndex: Int): BigInteger = with(instruction) {
            when (operation.paramModes[paramIndex]) {
                Position -> state.memory.address(parameters[paramIndex].toLong())
                Immediate -> parameters[paramIndex]
                Relative -> state.memory.address(parameters[paramIndex].toLong() + state.relativeBase)
            }
        }

        fun writeAndMovePointer(value: BigInteger, paramIndex: Int = 2): ComputerState = with(instruction) {
            when (operation.paramModes[paramIndex]) {
                Relative -> state.copy(memory = state.memory.update(instruction.parameters[paramIndex].toLong() + state.relativeBase to value), pointer = movePointer())
                else -> state.copy(memory = state.memory.update(instruction.parameters[paramIndex].toLong() to value), pointer = movePointer())
            }
        }

        fun binaryInstruction(f: (BigInteger, BigInteger) -> BigInteger): ComputerState = with(instruction) {
            val result = f(read(0), read(1))
            writeAndMovePointer(result)
        }

        fun Boolean.toBigInteger() = if (this) ONE else ZERO

        return when (instruction.operation.opCode) {
            1 -> binaryInstruction(BigInteger::plus)
            2 -> binaryInstruction(BigInteger::times)
            3 -> state.input?.let {
                writeAndMovePointer(it.toBigInteger(), 0).copy(input = null)
            } ?: state
            4 -> state.copy(pointer = movePointer(), output = state.output + read(0))
            5 -> {
                if (read(0) != ZERO) state.copy(pointer = read(1).toLong())
                else state.copy(pointer = movePointer())
            }
            6 -> {
                if (read(0) == ZERO) state.copy(pointer = read(1).toLong())
                else state.copy(pointer = movePointer())
            }
            7 -> writeAndMovePointer((read(0) < read(1)).toBigInteger())
            8 -> writeAndMovePointer((read(0) == read(1)).toBigInteger())
            9 -> state.copy(pointer = movePointer(), relativeBase = state.relativeBase + read(0).toLong())
            else -> state.copy(pointer = movePointer())
        }
    }

    val nextInstruction = nextInstruction(state)

    return if (nextInstruction == null) state else {
        if (nextInstruction.operation.opCode == 99 || (nextInstruction.operation.opCode == 3 && state.input == null)) state
        else doOps(processInstruction(nextInstruction))
    }
}