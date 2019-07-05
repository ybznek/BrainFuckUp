package brainfuckup.interpret

import brainfuckup.BrainFuckInterpreter
import java.util.*
import kotlin.collections.HashMap

// todo change ptr start to middle of block
open class BrainFuckCodeInterpreter() : BrainFuckInterpreter {

    public val blockPattern = 0xffff
    val memory = HashMap<Int, ShortArray>()
    var ptr = 0
    val inBlockAddress: Int
        get() = (ptr and blockPattern)

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
        getMemoryBlock()[inBlockAddress] = value.toShort()
    }

    fun setValue(ptrChange: Int, value: Int) {
        val addr = ((ptr + ptrChange) and blockPattern)
        val block = getMemoryBlock(addr)
        block[addr] = (value).toShort()
    }

    fun changeValue(value: Int) {
        val addr = inBlockAddress
        val block = getMemoryBlock()
        block[addr] = (block[addr] + value).toShort()


    }

    fun changeValue(ptrChange: Int, value: Int) {
        val addr = ((ptr + ptrChange) and blockPattern)
        val block = getMemoryBlock(addr)
        block[addr] = (block[addr] + value).toShort()
    }

    fun getValue(): Short {
        val block = getMemoryBlock()
        return block[inBlockAddress]

    }


    private fun getMemoryBlock(): ShortArray {
        return getMemoryBlock(ptr)
    }

    private fun getMemoryBlock(ptrValue: Int): ShortArray {
        val memoryBlock = (ptrValue and blockPattern.inv())
        return memory.computeIfAbsent(memoryBlock) {
            ShortArray(blockPattern + 1)
        }
    }


    override fun run(program: String) {
        val code = BrainFuckCode()
        code.load(program)
        runCode(code.instructions)
    }

    private fun runCode(code: List<BrainFuckCode.Instruction>) {

        for (instr in code) {
            when (instr) {
                is BrainFuckCode.MovePtr -> ptr += instr.diff
                is BrainFuckCode.ChangeVal -> changeValue(instr.diff)
                is BrainFuckCode.ChangeValPtr -> changeValue(instr.ptr, instr.value)
                is BrainFuckCode.SetValPtr -> setValue(instr.ptr, instr.value)
                is BrainFuckCode.Loop -> while (getValue() != 0.toShort()) runCode(instr.list)
                is BrainFuckCode.SetVal -> setValue(instr.value)
                is BrainFuckCode.Write -> write(getValue())
                is BrainFuckCode.Read -> setValue(read().toInt())
                is BrainFuckCode.NOP -> Unit
            }
        }

    }
}