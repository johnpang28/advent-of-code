package day11

import ComputerState
import asMemory
import day11.Direction.Down
import day11.Direction.Left
import day11.Direction.Right
import day11.Direction.Up
import day11.Paint.Black
import day11.Paint.White
import day11.Turn.TurnLeft
import day11.Turn.TurnRight
import doOps
import isAwaitingInput
import isCompleted
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

fun main() {
    val initialComputerState = ComputerState(data.asMemory(), 0L, 0L, emptyList())
    val initialRobot = RobotState(0, 0, Up)
    val (_, _, panels1) = doSteps(initialComputerState, initialRobot, emptyList(), Panel(0, 0))
    val answer1 = panels1.size
    println(answer1)

    val (_, _, panels2) = doSteps(initialComputerState, initialRobot, emptyList(), Panel(0, 0, White))
    render(panels2)
}

const val data = "3,8,1005,8,338,1106,0,11,0,0,0,104,1,104,0,3,8,102,-1,8,10,1001,10,1,10,4,10,1008,8,1,10,4,10,1002,8,1,29,2,105,19,10,1006,0,52,1,1009,7,10,1006,0,6,3,8,102,-1,8,10,101,1,10,10,4,10,108,1,8,10,4,10,1001,8,0,64,2,1002,19,10,1,8,13,10,1,1108,16,10,2,1003,1,10,3,8,102,-1,8,10,1001,10,1,10,4,10,1008,8,1,10,4,10,1002,8,1,103,1006,0,10,2,109,16,10,1,102,11,10,2,6,13,10,3,8,102,-1,8,10,1001,10,1,10,4,10,1008,8,0,10,4,10,1002,8,1,140,2,102,8,10,2,4,14,10,1,8,19,10,1006,0,24,3,8,1002,8,-1,10,101,1,10,10,4,10,1008,8,0,10,4,10,1001,8,0,177,1006,0,16,1,1007,17,10,3,8,102,-1,8,10,1001,10,1,10,4,10,108,1,8,10,4,10,101,0,8,205,3,8,1002,8,-1,10,1001,10,1,10,4,10,1008,8,0,10,4,10,102,1,8,228,1,1005,1,10,1,9,1,10,3,8,102,-1,8,10,101,1,10,10,4,10,1008,8,1,10,4,10,1002,8,1,258,3,8,1002,8,-1,10,1001,10,1,10,4,10,108,0,8,10,4,10,102,1,8,279,3,8,102,-1,8,10,1001,10,1,10,4,10,108,0,8,10,4,10,102,1,8,301,1,3,17,10,2,7,14,10,2,6,18,10,1,1001,17,10,101,1,9,9,1007,9,1088,10,1005,10,15,99,109,660,104,0,104,1,21102,1,48092525312,1,21101,355,0,0,1106,0,459,21102,665750184716,1,1,21102,366,1,0,1106,0,459,3,10,104,0,104,1,3,10,104,0,104,0,3,10,104,0,104,1,3,10,104,0,104,1,3,10,104,0,104,0,3,10,104,0,104,1,21102,1,235324768296,1,21101,0,413,0,1105,1,459,21101,3263212736,0,1,21102,424,1,0,1106,0,459,3,10,104,0,104,0,3,10,104,0,104,0,21102,1,709496824676,1,21101,447,0,0,1105,1,459,21102,988220904204,1,1,21102,1,458,0,1106,0,459,99,109,2,21201,-1,0,1,21102,40,1,2,21102,490,1,3,21102,1,480,0,1105,1,523,109,-2,2106,0,0,0,1,0,0,1,109,2,3,10,204,-1,1001,485,486,501,4,0,1001,485,1,485,108,4,485,10,1006,10,517,1101,0,0,485,109,-2,2105,1,0,0,109,4,2101,0,-1,522,1207,-3,0,10,1006,10,540,21102,0,1,-3,22101,0,-3,1,22102,1,-2,2,21102,1,1,3,21101,559,0,0,1106,0,564,109,-4,2105,1,0,109,5,1207,-3,1,10,1006,10,587,2207,-4,-2,10,1006,10,587,22102,1,-4,-4,1105,1,655,22101,0,-4,1,21201,-3,-1,2,21202,-2,2,3,21102,606,1,0,1105,1,564,21202,1,1,-4,21101,0,1,-1,2207,-4,-2,10,1006,10,625,21102,0,1,-1,22202,-2,-1,-2,2107,0,-3,10,1006,10,647,22101,0,-1,1,21101,647,0,0,105,1,522,21202,-2,-1,-2,22201,-4,-2,-4,109,-5,2106,0,0"

enum class Paint(val code: Int) { Black(0), White(1) }

enum class Direction { Up, Down, Left, Right }

enum class Turn { TurnLeft, TurnRight }

data class Panel(val x: Int, val y: Int, val paint: Paint = Black)

data class RobotState(val x: Int, val y: Int, val direction: Direction)

fun Direction.turnLeft(): Direction = when (this) {
    Up -> Left
    Down -> Right
    Left -> Down
    Right -> Up
}

fun Direction.turnRight(): Direction = when (this) {
    Up -> Right
    Down -> Left
    Left -> Up
    Right -> Down
}

fun BigInteger.toPaint(): Paint = when (this) {
    ZERO -> Black
    ONE -> White
    else -> throw Error("Unexpected paint value")
}

fun BigInteger.toTurn(): Turn = when (this) {
    ZERO -> TurnLeft
    ONE -> TurnRight
    else -> throw Error("Unexpected turn value")
}

fun RobotState.turnAndMoveForward(turn: Turn): RobotState {
    val newDirection = when (turn) {
        TurnLeft -> this.direction.turnLeft()
        TurnRight -> this.direction.turnRight()
    }
    return when (newDirection) {
        Up -> this.copy(y = this.y + 1, direction = newDirection)
        Down -> this.copy(y = this.y - 1, direction = newDirection)
        Left -> this.copy(x = this.x - 1, direction = newDirection)
        Right -> this.copy(x = this.x + 1, direction = newDirection)
    }
}

fun doNextStep(
    computerState: ComputerState,
    robot: RobotState,
    panels: List<Panel>,
    currentPanel: Panel
): Triple<ComputerState, RobotState, List<Panel>> {
    val input = currentPanel.paint.code.toLong()
    val updatedComputerState = doOps(computerState.copy(input = input, output = emptyList()))
    return if (updatedComputerState.output.size == 2) {
        val paint = updatedComputerState.output[0].toPaint()
        val turn = updatedComputerState.output[1].toTurn()
        val updatedPanels = panels.filterNot { panel -> panel.x == robot.x && panel.y == robot.y } + currentPanel.copy(paint = paint)
        val updatedRobot = robot.turnAndMoveForward(turn)
        Triple(updatedComputerState, updatedRobot, updatedPanels)
    } else throw Error("Unexpected output size!")
}

tailrec fun doSteps(
    computerState: ComputerState,
    robot: RobotState,
    panels: List<Panel>,
    currentPanel: Panel
): Triple<ComputerState, RobotState, List<Panel>> = when {
    isCompleted(computerState) -> Triple(computerState, robot, panels)
    isAwaitingInput(computerState) -> {
        val (updatedComputer, updatedRobot, updatedPanels) = doNextStep(computerState, robot, panels, currentPanel)
        val nextPanel = updatedPanels.firstOrNull { panel -> panel.x == updatedRobot.x && panel.y == updatedRobot.y } ?: Panel(updatedRobot.x, updatedRobot.y)
        doSteps(updatedComputer, updatedRobot, updatedPanels, nextPanel)
    }
    else -> throw Error("Unexpected state")
}

fun render(panels: List<Panel>) {
    if (panels.isNotEmpty()) {
        val minX = panels.minBy { it.x }!!.x
        val maxX = panels.maxBy { it.x }!!.x
        val minY = panels.minBy { it.y }!!.y
        val maxY = panels.maxBy { it.y }!!.y

        maxY.downTo(minY).forEach { y ->
            (minX..maxX).forEach { x ->
                val paint = panels.firstOrNull { it.x == x && it.y == y }?.paint ?: Black
                print(if (paint == White) "#" else ".")
            }
            println()
        }
    }
}

