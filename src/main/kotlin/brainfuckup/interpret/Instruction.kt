package brainfuckup.interpret

interface Instruction {

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

    class SetValWrite(var value: Int) : Instruction {
        override fun toString(): String {
            return "setValWrite($value)"
        }

        override fun equals(other: Any?): Boolean {
            return (other as? SetValWrite)?.value == this.value
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

}