package brainfuckup.interpret

import brainfuckup.interpret.Instruction.*
import java.util.*
import kotlin.collections.ArrayList

class BrainFuckCode {


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
                '.' -> {
                    when (last) {
                        is SetVal -> {
                            insts.pop()
                            insts.push(SetValWrite(last.value))
                        }
                        is ChangeVal -> {
                            insts.pop()
                            when (val secondLast = insts.topOrNOP()) {
                                is SetValWrite -> {
                                    insts.pop()
                                    insts.push(
                                        SetValWriteString(
                                            arrayListOf(
                                                secondLast.value,
                                                secondLast.value + last.diff
                                            )
                                        )
                                    )
                                }
                                is SetValPtr -> {
                                    insts.pop()
                                    insts.push(MovePtr(secondLast.ptr))
                                    insts.push(
                                        SetValWriteString(
                                            arrayListOf(
                                                secondLast.value,
                                                secondLast.value + last.diff
                                            )
                                        )
                                    )
                                }
                                is SetValWriteString -> {
                                    secondLast.list.add(secondLast.list.last() + last.diff)
                                }
                                else -> {
                                    insts.push(ChangeValWrite(last.diff))
                                }
                            }
                        }
                        is SetValPtr -> {
                            insts.pop()
                            insts.push(MovePtr(last.ptr))
                            insts.push(SetValWrite(last.value))
                        }
                        else -> insts.push(Write)
                    }
                }
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

                            when (val topOrNOP = insts.topOrNOP()) {
                                is SetVal, is ChangeVal -> insts.pop() // this instruction has no effect
                                is ChangeValPtr -> {
                                    insts.pop()
                                    insts.push(MovePtr(topOrNOP.ptr))
                                }
                                is SetValPtr -> {
                                    insts.pop()
                                    insts.push(MovePtr(topOrNOP.ptr))
                                }
                            }

                            when (val topOrNOP = insts.topOrNOP()) {
                                is MovePtr -> {
                                    insts.pop()
                                    insts.push(SetValPtr(topOrNOP.diff, 0))
                                }
                                else -> insts.push(SetVal(0))
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