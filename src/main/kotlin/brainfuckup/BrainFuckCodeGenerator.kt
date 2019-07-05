package brainfuckup

class BrainFuckCodeGenerator {

    val outputBuffer = ArrayList<Char>()

    fun <A> MutableList<A>.pop() {
        outputBuffer.removeAt(outputBuffer.size - 1)
    }

    fun MutableList<Char>.top(): Char {
        return if (this.isEmpty())
            ' '
        else
            this[this.size - 1]
    }

    /**
     * append instructions to output and do simple optimization
     */

    fun append(v: String) {
        for (newChar in v) {
            val top = outputBuffer.top()
            when (Pair(top, newChar)) {
                Pair('+', '-'), Pair('-', '+'), Pair('<', '>'), Pair('>', '<') -> outputBuffer.pop()
                else -> outputBuffer.add(newChar)
            }
        }
    }

    fun program(func: BrainFuckCodeGenerator.() -> Unit) {
        func(this)
    }

    fun reset(shift: Int = 0) {
        move(shift) {
            append("[-]")
        }
    }

    fun move(shift: Int, func: BrainFuckCodeGenerator.() -> Unit) {
        movePtr(shift)
        func(this)
        movePtr(-shift)
    }

    fun write() {
        append(".")
    }

    fun read() {
        append(",")
    }

    fun inc(cnt: Int = 1) {
        repeat(cnt) {
            append("+")
        }
    }

    fun dec(cnt: Int = 1) {
        repeat(cnt) {
            append("-")
        }
    }

    fun eq(to: Int, from: Int, tmp0: Int, tmp1: Int) {
        return eqOrNeqOld(to, from, tmp0, tmp1, true)
    }

    fun neq(to: Int, from: Int, tmp0: Int, tmp1: Int) {
        return eqOrNeqOld(to, from, tmp0, tmp1, false)
    }

    /*
    * Move to 0 before loo
    * move to 0 after loop
    *
    * */
    fun decLoop(loopVar: Int, f: BrainFuckCodeGenerator.() -> Unit) {
        moveTo(0, loopVar)
        loop {
            moveTo(loopVar, 0)
            f()
            moveTo(0, loopVar)
            dec()
        }
        moveTo(loopVar, 0)
    }

    fun addTo(target: Int, v1: Int, v2: Int, t1: Int) {
        assign(target, v1, t1)
        add(target, v2, t1)
    }

    private fun eqOrNeqOld(x: Int, y: Int, tmp0: Int, tmp1: Int, eq: Boolean) {

        reset(tmp0)
        reset(tmp1)

        decLoop(x) {
            inc(tmp1, 1)
        }

        if (eq) {
            inc(x, 1)
        }

        decLoop(y) {
            dec(tmp1, 1)
            inc(tmp0, 1)
        }

        decLoop(tmp0) {
            inc(y, 1)
        }

        moveTo(0, tmp1)
        loop {
            moveTo(tmp1, x)
            if (eq) {
                dec(1)
            } else {
                inc(1)
            }
            moveTo(x, tmp1)
            reset()
        }
        moveTo(tmp1, 0)
    }

    fun inc(relativeIndex: Int, cnt: Int = 1) {
        move(relativeIndex) {
            repeat(cnt) {
                append("+")
            }
        }
    }

    fun dec(relativeIndex: Int, cnt: Int = 1) {
        move(relativeIndex) {
            repeat(cnt) {
                append("-")
            }
        }
    }

    fun incPtr(cnt: Int = 1) {
        repeat(cnt) {
            append(">")
        }
    }

    fun decPtr(cnt: Int = 1) {

        repeat(cnt) {
            append("<")
        }
    }

    fun moveTo(from: Int, to: Int) {
        val rel = to - from
        movePtr(rel)
    }

    fun movePtr(value: Int) {
        if (value < 0) {
            decPtr(-value)
        } else {
            incPtr(value)
        }
    }

    fun write(relativeIndex: Int) {
        move(relativeIndex) {
            write()
        }
    }

    fun assign(to: Int, from: Int, tmp: Int) {
        program {
            // temp0[-]
            moveTo(0, tmp)
            reset()

            // to[-]
            moveTo(tmp, to)
            reset()

            // from
            moveTo(to, from)
            loop {
                // to+
                moveTo(from, to)
                inc()
                // temp0 +
                moveTo(to, tmp)
                inc()

                // from-
                moveTo(tmp, from)
                dec()
            }

            // temp0
            moveTo(from, tmp)
            loop {
                // from+
                moveTo(tmp, from)
                inc()
                // temp-
                moveTo(from, tmp)
                dec()
            }
            // start
            moveTo(tmp, 0)
        }
    }

    fun div(x: Int, y: Int, tmp0: Int, tmp1: Int, tmp2: Int, tmp3: Int) {
        reset(tmp0)
        reset(tmp1)
        reset(tmp2)
        reset(tmp3)

        decLoop(x) {
            inc(tmp0, 1)
        }

        moveTo(0, tmp0)
        loop {
            moveTo(tmp0, 0)
            decLoop(y) {
                inc(tmp1, 1)
                inc(tmp2, 1)
            }
            decLoop(tmp2) {
                inc(y, 1)
            }

            decLoop(tmp1) {
                inc(tmp2, 1)
                dec(tmp0, 1)
                decLoop(tmp0) {
                    reset(tmp2)
                    inc(tmp3, 1)
                }
                decLoop(tmp3) {
                    inc(tmp0, 1)
                }
                decLoop(tmp2) {
                    moveTo(0, tmp1)
                    dec()
                    loop {
                        moveTo(tmp1, x)
                        dec()
                        moveTo(x, tmp1)
                        reset()
                    }
                    inc()
                    moveTo(tmp1, 0)

                }
            }
            inc(x, 1)
            moveTo(0, tmp0)
        }
        moveTo(tmp0, 0)
    }

    fun mult(x: Int, y: Int, temp0: Int, temp1: Int) {

        moveTo(0, temp0)
        reset()

        moveTo(temp0, temp1)
        reset()

        moveTo(temp1, x)
        loop {
            moveTo(x, temp1)
            inc()
            moveTo(temp1, x)
            dec()
        }
        moveTo(x, temp1)
        loop {
            moveTo(temp1, y)
            loop {
                moveTo(y, x)
                inc()
                moveTo(x, temp0)
                inc()
                moveTo(temp0, y)
                dec()
            }
            moveTo(y, temp0)
            loop {
                moveTo(temp0, y)
                inc()
                moveTo(y, temp0)
                dec()
            }

            moveTo(temp0, temp1)
            dec()
        }
        moveTo(temp1, 0)

    }

    fun addOrSub(to: Int, from: Int, tmp: Int, inc: Boolean) {
        program {
            moveTo(0, tmp)
            reset()
            moveTo(tmp, from)
            loop {
                moveTo(from, to)
                if (inc) {
                    inc()
                } else {
                    dec()
                }
                moveTo(to, tmp)
                inc()
                moveTo(tmp, from)
                dec()
            }
            moveTo(from, tmp)
            loop {
                moveTo(tmp, from)
                inc()
                moveTo(from, tmp)
                dec()
            }
            moveTo(tmp, 0)
        }
    }

    fun add(to: Int, from: Int, tmp: Int) {
        addOrSub(to, from, tmp, true)
    }

    fun sub(to: Int, from: Int, tmp: Int) {
        addOrSub(to, from, tmp, false)
    }

    fun set(char: Char) {
        set(char.toInt())
    }

    fun set(relativeIndex: Int, char: Char) {
        set(relativeIndex, char.toInt())
    }

    fun set(relativeIndex: Int, value: Int) {
        movePtr(relativeIndex)
        set(value)
        movePtr(-relativeIndex)
    }

    fun swap(v1: Int, v2: Int, tmp0: Int, tmp1: Int) {
        assign(tmp0, v1, tmp1)
        assign(v1, v2, tmp1)
        assign(v2, tmp0, tmp1)
    }

    fun set(value: Int) {
        checkByte(value);

        reset()
        inc(value)
    }

    fun addToNext() {
        append("[->+<]")
    }

    fun loop(func: BrainFuckCodeGenerator.() -> Unit) {
        append("[")
        func(this);
        append("]")
    }

    fun readAll() {
        append(",[>,]")
    }

    fun checkByte(value: Int) {
        if (value > 255 || value < 0) throw IndexOutOfBoundsException("value")
    }

    override fun toString(): String {
        return outputBuffer.joinToString("")
    }

    fun and(x: Int, y: Int, tmp0: Int, tmp1: Int) {
        reset(tmp0)
        reset(tmp1)

        decLoop(x) {
            inc(tmp1, 1)
        }

        moveTo(0, tmp1)
        loop {
            reset()
            moveTo(tmp1, 0)
            decLoop(y) {
                inc(tmp1, 1)
                inc(tmp0, 1)
            }
            decLoop(tmp0) {
                inc(y, 1)
            }

            moveTo(0, tmp1)
            loop {
                moveTo(tmp1, x)
                inc()
                moveTo(x, tmp1)
                reset()
            }
        }

        moveTo(tmp1, 0)
    }

    fun gt(z: Int, x: Int, y: Int, tmp0: Int, tmp1: Int) {
        reset(tmp0)
        reset(tmp1)
        reset(z)

        moveTo(0, x)
        loop {
            moveTo(x, tmp0)
            inc()
            moveTo(tmp0, y)
            loop {
                dec()
                moveTo(y, tmp0)
                reset()
                moveTo(tmp0, tmp1)
                inc()
                moveTo(tmp1, y)
            }

            moveTo(y, tmp0)
            loop {
                dec()
                moveTo(tmp0, z)
                inc()
                moveTo(z, tmp0)
            }
            moveTo(tmp0, tmp1)
            loop {
                dec()
                moveTo(tmp1, y)
                inc()
                moveTo(y, tmp1)
            }
            moveTo(tmp1, y)
            dec()
            moveTo(y, x)
            dec()
        }

        moveTo(x, 0)
    }
}