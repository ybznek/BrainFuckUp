package brainfuckup.interpret

import brainfuckup.BrainFuckInterpreter

// todo change ptr start to middle of block
abstract class BrainFuckCodeInterpreter<T : Number, Arr>() : BrainFuckInterpreter<T> {

    public val blockPattern = 0xffff
    val memory = HashMap<Int, Arr>()
    var ptr = 0
    val inBlockAddress: Int
        get() = (ptr and blockPattern)

    fun setValue(value: Int) {
        setBlockValue(getMemoryBlock(), inBlockAddress, value)
    }

    fun setValue(ptrChange: Int, value: Int) {
        val addr = ((ptr + ptrChange) and blockPattern)
        val block = getMemoryBlock(addr)
        setBlockValue(block, addr, value)
    }

    fun changeValue(value: Int) {
        val addr = inBlockAddress
        val block = getMemoryBlock()
        incBlockValue(block, addr, value)


    }

    fun changeValue(ptrChange: Int, value: Int) {
        val addr = ((ptr + ptrChange) and blockPattern)
        val block = getMemoryBlock(addr)
        incBlockValue(block, addr, value)
    }

    fun getValue(): T {
        val block = getMemoryBlock()
        return getBlockValue(block, inBlockAddress)
    }

    abstract fun getBlockValue(block: Arr, index: Int): T;
    abstract fun setBlockValue(block: Arr, index: Int, value: Int);
    abstract fun incBlockValue(block: Arr, index: Int, value: Int);

    private fun getMemoryBlock(): Arr {
        return getMemoryBlock(ptr)
    }

    private fun getMemoryBlock(ptrValue: Int): Arr {
        val memoryBlock = (ptrValue and blockPattern.inv())
        return memory.computeIfAbsent(memoryBlock) {
            getArr(blockPattern + 1)
        }
    }

    private fun getArr(size: Int): Arr {
        return ShortArray(size) as Arr
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
                is Instruction.SetValWriteString -> {
                    for (value in instr.list) {
                        write(value as T) // todo
                    }
                    setValue(instr.list.last())
                }
                is Instruction.Read -> setValue(read().toInt())
                is Instruction.NOP -> Unit
            }
        }
    }
}