package brainfuckup.interpret

import java.util.*

open class BrainFuckCodeInterpreterShortCell : BrainFuckCodeInterpreter<Short, ShortArray>() {
    override fun write(v: Short) {
        print(v.toChar())
    }

    override fun getBlockValue(block: ShortArray, index: Int): Short {
        return block[index].toShort()
    }

    override fun setBlockValue(block: ShortArray, index: Int, value: Int) {
        block[index] = value.toShort()
    }

    override fun incBlockValue(block: ShortArray, index: Int, value: Int) {
        block[index] = (block[index] + value).toShort()
    }

    override fun read(): Short {
        return System.`in`.read().toShort()
    }

    override fun getMemoryDump(): SortedMap<Int, Short> {

        val ret = TreeMap<Int, Short>()
        for ((k, block) in memory.entries.sortedBy { x -> x.key }) {
            for ((index, value) in block.withIndex()) {
                ret[(k or index)] = value
            }
        }
        return ret

    }
}