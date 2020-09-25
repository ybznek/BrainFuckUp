package brainfuckup.interpret

import java.util.*
import kotlin.collections.ArrayList

class BrainFuckCode {

    interface Instruction
    class MovePtr(var diff: Int) : Instruction {

        override fun equals(other: Any?): Boolean {
            return (other as? MovePtr)?.diff == this.diff
        }

        override fun toString(): String {
            return "MovePtr($diff)"
        }

        override fun hashCode(): Int {
            return diff
        }
    }

    data class ChangeVal(var diff: Int) : Instruction {

        override fun equals(other: Any?): Boolean {
            return (other as? ChangeVal)?.diff == this.diff
        }

        override fun toString(): String {
            return "ChangeVal($diff)"
        }

        override fun hashCode(): Int {
            return diff
        }
    }

    class Loop(val list: MutableList<Instruction> = ArrayList()) : Instruction {
        override fun toString(): String {
            return this.list.joinToString(",", "loop(", ")") { x -> x.toString() }
        }
    }

    class SetVal(var value: Int) : Instruction {
        override fun toString(): String {
            return "setVal($value)"
        }

        override fun equals(other: Any?): Boolean {
            return (other as? SetVal)?.value == this.value
        }

        override fun hashCode(): Int {
            return value
        }
    }

    class ChangeValPtr(var ptr: Int, var value: Int) : Instruction {
        override fun toString(): String {
            return "changeValPtr($ptr, $value)"
        }

        override fun equals(other: Any?): Boolean {
            val o = other as? ChangeValPtr ?: return false
            return (o.value == value) && (o.ptr == ptr)
        }

        override fun hashCode(): Int {
            var result = ptr
            result = 31 * result + value
            return result
        }
    }

    class SetValPtr(var ptr: Int, var value: Int) : Instruction {
        override fun toString(): String {
            return "setValPtr($ptr, $value)"
        }

        override fun equals(other: Any?): Boolean {
            val o = other as? SetValPtr ?: return false
            return (o.value == value) && (o.ptr == ptr)
        }

        override fun hashCode(): Int {
            var result = ptr
            result = 31 * result + value
            return result
        }
    }

    object Write : Instruction {
        override fun toString(): String {
            return "write()"
        }
    }

    object Read : Instruction {
        override fun toString(): String {
            return "read()"
        }
    }

    object NOP : Instruction


    val instructions = ArrayList<Instruction>()

    fun <T> MutableList<T>.push(value: T) {
        this.add(value)
    }

    fun <T> MutableList<T>.pop(): T {
        val lastIndex = this.size - 1
        val retVal = this[lastIndex]
        removeAt(lastIndex)
        return retVal
    }

    fun <T> MutableList<T>.top(): T {
        val lastIndex = this.size - 1
        return this[lastIndex]
    }

    fun MutableList<Instruction>.topOrNOP(): Instruction {
        return if (this.isEmpty()) return NOP else this.top()
    }



    fun load(program: String) {

        val instrStack = Stack<MutableList<Instruction>>()
        instrStack.push(instructions)

        for (c in program) {
            var insts = instrStack.top()
            val last = if (insts.isNotEmpty())
                insts.top()
            else
                NOP

            when (c) {
                '+' -> when (last) {
                    is ChangeVal -> last.diff++
                    is SetVal -> last.value++
                    is MovePtr -> {
                        insts.pop()
                        insts.push(ChangeValPtr(last.diff, 1))
                    }
                    is ChangeValPtr -> {
                        last.value++
                    }
                    is SetValPtr -> {
                        last.value++
                    }
                    else -> insts.push(ChangeVal(1))
                }
                '-' -> when (last) {
                    is ChangeVal -> last.diff--
                    is SetVal -> last.value--
                    is MovePtr -> {
                        insts.pop()
                        insts.push(ChangeValPtr(last.diff, -1))
                    }
                    is ChangeValPtr -> {
                        last.value--
                    }
                    is SetValPtr -> {
                        last.value--
                    }
                    else -> insts.push(ChangeVal(-1))
                }
                '>' -> when (last) {
                    is MovePtr -> last.diff++
                    else -> insts.push(MovePtr(1))
                }
                '<' -> when (last) {
                    is MovePtr -> last.diff--
                    else -> insts.push(MovePtr(-1))
                }
                '.' -> insts.push(Write)
                ',' -> insts.push(Read)
                '[' -> {
                    val newLoop = Loop()
                    insts.push(newLoop)
                    instrStack.push(newLoop.list)
                }
                ']' -> {
                    instrStack.pop()
                    insts = instrStack.top()
                    val lastLoop = insts.top() as Loop
                    if (lastLoop.list.size == 1) {
                        val instruction: Instruction = lastLoop.list[0]
                        if (instruction is ChangeVal && instruction.diff == -1) {
                            insts.pop()
                            if (insts.isNotEmpty()) {
                                when (insts.top()) {
                                    is SetVal, is ChangeVal -> insts.pop() // this instruction has no effect
                                }
                            }
                            val topStack = instrStack.top()

                            when (val topOrNOP = topStack.topOrNOP()) {
                                is MovePtr -> {
                                    topStack.pop()
                                    topStack.push(SetValPtr(topOrNOP.diff, 0))
                                }
                                else -> topStack.push(SetVal(0))
                            }

                        }
                    }
                }
            }

        }
    }

    override fun toString(): String {
        return instructions.joinToString(",") { x -> x.toString() }
    }

}