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
        movePtrFromTo(0, loopVar)
        loop {
            movePtrFromTo(loopVar, 0)
            f()
            movePtrFromTo(0, loopVar)
            dec()
        }
        movePtrFromTo(loopVar, 0)
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

        movePtrFromTo(0, tmp1)
        loop {
            movePtrFromTo(tmp1, x)
            if (eq) {
                dec(1)
            } else {
                inc(1)
            }
            movePtrFromTo(x, tmp1)
            reset()
        }
        movePtrFromTo(tmp1, 0)
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

    fun movePtrFromTo(from: Int, to: Int) {
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
            movePtrFromTo(0, tmp)
            reset()

            // to[-]
            movePtrFromTo(tmp, to)
            reset()

            // from
            movePtrFromTo(to, from)
            loop {
                // to+
                movePtrFromTo(from, to)
                inc()
                // temp0 +
                movePtrFromTo(to, tmp)
                inc()

                // from-
                movePtrFromTo(tmp, from)
                dec()
            }

            // temp0
            movePtrFromTo(from, tmp)
            loop {
                // from+
                movePtrFromTo(tmp, from)
                inc()
                // temp-
                movePtrFromTo(from, tmp)
                dec()
            }
            // start
            movePtrFromTo(tmp, 0)
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

        movePtrFromTo(0, tmp0)
        loop {
            movePtrFromTo(tmp0, 0)
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
                    movePtrFromTo(0, tmp1)
                    dec()
                    loop {
                        movePtrFromTo(tmp1, x)
                        dec()
                        movePtrFromTo(x, tmp1)
                        reset()
                    }
                    inc()
                    movePtrFromTo(tmp1, 0)

                }
            }
            inc(x, 1)
            movePtrFromTo(0, tmp0)
        }
        movePtrFromTo(tmp0, 0)
    }

    fun mult(x: Int, y: Int, temp0: Int, temp1: Int) {

        movePtrFromTo(0, temp0)
        reset()

        movePtrFromTo(temp0, temp1)
        reset()

        movePtrFromTo(temp1, x)
        loop {
            movePtrFromTo(x, temp1)
            inc()
            movePtrFromTo(temp1, x)
            dec()
        }
        movePtrFromTo(x, temp1)
        loop {
            movePtrFromTo(temp1, y)
            loop {
                movePtrFromTo(y, x)
                inc()
                movePtrFromTo(x, temp0)
                inc()
                movePtrFromTo(temp0, y)
                dec()
            }
            movePtrFromTo(y, temp0)
            loop {
                movePtrFromTo(temp0, y)
                inc()
                movePtrFromTo(y, temp0)
                dec()
            }

            movePtrFromTo(temp0, temp1)
            dec()
        }
        movePtrFromTo(temp1, 0)

    }

    fun addOrSub(to: Int, from: Int, tmp: Int, inc: Boolean) {
        program {
            movePtrFromTo(0, tmp)
            reset()
            movePtrFromTo(tmp, from)
            loop {
                movePtrFromTo(from, to)
                if (inc) {
                    inc()
                } else {
                    dec()
                }
                movePtrFromTo(to, tmp)
                inc()
                movePtrFromTo(tmp, from)
                dec()
            }
            movePtrFromTo(from, tmp)
            loop {
                movePtrFromTo(tmp, from)
                inc()
                movePtrFromTo(from, tmp)
                dec()
            }
            movePtrFromTo(tmp, 0)
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

        movePtrFromTo(0, tmp1)
        loop {
            reset()
            movePtrFromTo(tmp1, 0)
            decLoop(y) {
                inc(tmp1, 1)
                inc(tmp0, 1)
            }
            decLoop(tmp0) {
                inc(y, 1)
            }

            movePtrFromTo(0, tmp1)
            loop {
                movePtrFromTo(tmp1, x)
                inc()
                movePtrFromTo(x, tmp1)
                reset()
            }
        }

        movePtrFromTo(tmp1, 0)
    }

    fun gt(z: Int, x: Int, y: Int, tmp0: Int, tmp1: Int) {
        reset(tmp0)
        reset(tmp1)
        reset(z)

        movePtrFromTo(0, x)
        loop {
            movePtrFromTo(x, tmp0)
            inc()
            movePtrFromTo(tmp0, y)
            loop {
                dec()
                movePtrFromTo(y, tmp0)
                reset()
                movePtrFromTo(tmp0, tmp1)
                inc()
                movePtrFromTo(tmp1, y)
            }

            movePtrFromTo(y, tmp0)
            loop {
                dec()
                movePtrFromTo(tmp0, z)
                inc()
                movePtrFromTo(z, tmp0)
            }
            movePtrFromTo(tmp0, tmp1)
            loop {
                dec()
                movePtrFromTo(tmp1, y)
                inc()
                movePtrFromTo(y, tmp1)
            }
            movePtrFromTo(tmp1, y)
            dec()
            movePtrFromTo(y, x)
            dec()
        }

        movePtrFromTo(x, 0)
    }
}