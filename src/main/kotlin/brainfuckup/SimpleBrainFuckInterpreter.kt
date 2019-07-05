package brainfuckup

import java.util.*
import kotlin.collections.HashMap

interface BrainFuckInterpreter {
    fun write(v: Short) = print(v.toChar())
    fun read(): Short = System.`in`.read().toShort()
    fun run(program: String);
}

open class SimpleBrainFuckInterpreter :BrainFuckInterpreter{

    override fun write(v: Short) = print(v.toChar())
    override fun read(): Short = System.`in`.read().toShort()

    val memory = HashMap<Int, Short>()
    var ptr: Int = 0
    val loopStack = Stack<Int>()

    fun get(): Short {
        return memory.computeIfAbsent(ptr) { 0 }
    }

    fun set(value: Short) {
        memory[ptr] = value
    }

    fun inc() {
        memory.compute(ptr) { _, value ->
            value?.inc() ?: 1
        }
    }

    fun dec() {
        memory.compute(ptr) { _, value ->
            value?.dec() ?: -1
        }
    }

    override fun run(program: String) {
        memory.clear()
        ptr = 0
        val length = program.length
        var index = 0
        while (index < length) {

            val c = program[index]
            when (c) {
                '>' -> ++ptr
                '<' -> --ptr
                '+' -> inc()
                'D' -> {
                    val debug = 42
                }
                '-' -> dec()
                '.' -> write(get())
                ',' -> set(read())
                '[' -> if (get() == 0.toShort()) {
                    var skipEnd = 0

                    innerLoop@ while (true) {

                        when (program[index]) {
                            ']' -> if (skipEnd <= 1) {
                                break@innerLoop
                            } else {
                                skipEnd--
                            }
                            '[' -> skipEnd++
                        }
                        ++index

                    }
                } else {
                    loopStack.push(index)
                }

                ']' -> if (get() != 0.toShort()) {
                    index = loopStack.pop() - 1
                } else {
                    loopStack.pop()
                }
            }
            ++index
        }

    }
}