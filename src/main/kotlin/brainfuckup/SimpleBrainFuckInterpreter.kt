package brainfuckup

import java.util.*
import java.util.Stack
import kotlin.collections.HashMap

interface BrainFuckInterpreter<T : Number> {
    fun write(v: T);
    fun read(): T;
    fun run(program: String);

    fun getMemoryDump(): SortedMap<Int, T>;
}

open class SimpleBrainFuckInterpreter : BrainFuckInterpreter<Short> {

    override fun getMemoryDump(): SortedMap<Int, Short> {
        val s = TreeMap<Int, Short>()
        val entries = memory.entries.sortedBy { x -> x.key }
        for ((k, v) in entries) {
            s[k] = v
        }
        return s;
    }

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