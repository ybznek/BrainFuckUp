package brainfuckup.interpret

import brainfuckup.BrainFuckInterpreter
import java.util.*
import kotlin.collections.HashMap

// todo change ptr start to middle of block
open class BrainFuckCodeInterpreter() : BrainFuckInterpreter {

    public val blockPattern = 0xffff
    val memory = HashMap<Int, ShortArray>()
    var ptr = 0

    private fun getBlockAddressFromPtr(ptrValue: Int) = (ptrValue and blockPattern.inv())
    private fun getInBlockAddressFromPtr(ptrValue: Int) = (ptrValue and blockPattern)

    private fun getInBlockAddressFromPtr() = getInBlockAddressFromPtr(ptr)

    fun getMemoryDump(): SortedMap<Int, Short> {
        val ret = TreeMap<Int, Short>()
        for ((k, block) in memory.entries.sortedBy { x -> x.key }) {
            for ((index, value) in block.withIndex()) {
                ret[(k or index)] = value
            }
        }
        return ret;
    }

    fun setValue(value: Int) {
        getMemoryBlock()[getInBlockAddressFromPtr()] = value.toShort()
    }


    fun changeValue(value: Int) {
        val block = getMemoryBlock()
        val inBlock = getInBlockAddressFromPtr()
        block[inBlock] = (block[inBlock] + value).toShort()
    }


    fun getValue(): Short {
        val block = getMemoryBlock()
        return block[getInBlockAddressFromPtr()]
    }

    private fun getMemoryBlock(): ShortArray {
        return getMemoryBlock(ptr)
    }

    private fun getMemoryBlock(ptrValue: Int): ShortArray {
        val memoryBlock = getBlockAddressFromPtr(ptrValue)
        return memory.computeIfAbsent(memoryBlock) {
            ShortArray(blockPattern + 1)
        }
    }

    override fun run(program: String) {
        val code = BrainFuckCode()
        code.load(program)
        runCode(code.instructions)
    }

    private fun runCode(code: List<Instruction>) {

        for (instr in code) {
            when (instr) {
                is Instruction.MovePtr -> ptr += instr.diff
                is Instruction.ChangeVal -> changeValue(instr.diff)
                is Instruction.ChangeValPtr -> {
                    ptr += instr.ptr
                    changeValue(instr.value)
                }
                is Instruction.SetValPtr -> {
                    ptr += instr.ptr
                    setValue(instr.value)
                }
                is Instruction.Loop -> while (getValue() != 0.toShort()) runCode(instr.list)
                is Instruction.SetVal -> setValue(instr.value)
                is Instruction.Write -> write(getValue())
                is Instruction.SetValWrite -> {
                    setValue(instr.value)
                    write(getValue())
                }
                is Instruction.ChangeValWrite -> {
                    changeValue(instr.diff)
                    write(getValue())
                }
                is Instruction.Read -> setValue(read().toInt())
                is Instruction.NOP -> Unit
            }
        }
    }
}