package brainfuckup

import brainfuckup.interpret.BrainFuckCodeInterpreter

class TestInterpreterSimple() : BrainFuckCodeInterpreter() {

    var inputData = charArrayOf()
    var inputIndex = 0

    private val outputBuffer = StringBuilder()

    fun setInput(input: String) {
        inputData = input.toCharArray()
    }

    override fun read(): Short {
        return if (inputIndex >= inputData.size) {
            (-1).toShort()
        } else {
            inputData[inputIndex].toShort()
        }
    }

    override fun write(v: Short) {
        outputBuffer.append(v.toChar())
    }

    val output: String
        get() = outputBuffer.toString()
}