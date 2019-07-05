package brainfuckup

import brainfuckup.expression.*


class BrainFuckMachine() {

    data class Quadruple<T1, T2, T3, T4>(val v1: T1, val v2: T2, val v3: T3, val v4: T4);
    data class Tuple<T1, T2, T3, T4, T5>(val v1: T1, val v2: T2, val v3: T3, val v4: T4, val v5: T5);


    val bf = BrainFuckCodeGenerator()

    private val registerList = (-1 downTo -100).toSet()

    private val usedRegister = mutableSetOf<Int>()

    private fun getFreeRegisterSet(vararg index: Int): Set<Int> {
        return registerList
            .minus(usedRegister)
            .minus(index.toSet())
    }

    private fun getFreeRegister(vararg index: Int): Int {
        return getFreeRegisterSet(*index).first()
    }

    private fun getFreeRegister2(vararg index: Int): Pair<Int, Int> {
        val frees = getFreeRegisterSet(*index).toList()
        return Pair(frees[0], frees[1])
    }

    private fun getFreeRegister3(vararg index: Int): Triple<Int, Int, Int> {
        val frees = getFreeRegisterSet(*index).toList()
        return Triple(frees[0], frees[1], frees[2])
    }

    private fun getFreeRegister4(vararg index: Int): Quadruple<Int, Int, Int, Int> {
        val frees = getFreeRegisterSet(*index).toList()
        return Quadruple(frees[0], frees[1], frees[2], frees[3])
    }

    private fun getFreeRegister5(vararg index: Int): Tuple<Int, Int, Int, Int, Int> {
        val frees = getFreeRegisterSet(*index).toList()
        return Tuple(frees[0], frees[1], frees[2], frees[3], frees[4])
    }

    operator fun Expression.plus(v: Int): Expression {
        return Formula(
            ExpressionType.Plus,
            this,
            Constant(v)
        )
    }

    infix operator fun Expression.div(v: Int): Expression {
        return Formula(
            ExpressionType.Div,
            this,
            Constant(v)
        )
    }

    infix operator fun Expression.div(second: Expression): Expression {
        return Formula(ExpressionType.Div, this, second)
    }

    fun read(e: Variable) {
        bf.move(e.index) {
            bf.read()
        }
    }

    infix fun Expression.gte(e2: Expression) = (this gt e2) or (this eq e2)
    infix fun Expression.gte(value: Int) = this gte Constant(
        value
    )

    infix fun Expression.gte(value: Char) = this gte Constant(
        value.toInt()
    )

    infix fun Expression.lt(e2: Expression) = (e2 gt this)
    infix fun Expression.lte(e2: Expression) = (e2 gt this) or (this eq e2)
    infix fun Expression.lte(value: Int) = this lte Constant(
        value
    )

    infix fun Expression.lte(value: Char) = this lte Constant(
        value.toInt()
    )

    operator fun Expression.plus(v: Char): Expression {
        return this + v.toInt()
    }

    var cnt = 1

    fun declare(size: Int = 1, func: BrainFuckMachine.(Variable) -> Unit) {
        val v = Variable(cnt)
        cnt += size
        for (i in 0 until size) {
            Variable(v.index + i) set 0
        }
        func(v)
        cnt -= size
    }

    infix fun Variable.set(value: Int) {
        this@BrainFuckMachine[this] = value
    }

    infix fun Variable.set(expr: Expression) {
        this@BrainFuckMachine[this] = evaluate(expr)
    }

    @JvmName("setOperatorInt")
    operator fun set(v: Variable, value: Int) {
        bf.set(v.index, value)
    }

    operator fun set(v: Variable, c: Constant) {
        bf.set(v.index, c.value)
    }

    @JvmName("setOperatorVariable")
    operator fun set(v: Variable, second: Expression) {
        val exprVariable = evaluate(second)
        blockRegister(exprVariable) {
            val tmp = getFreeRegister(v.index)
            bf.assign(v.index, exprVariable.index, tmp)
        }
    }

    fun useStack(size: Int, func: BrainFuckMachine.(Stack) -> Unit) {
        // todo these manipulations with cnt/size are not correct probably

        val stackStart = cnt
        cnt += size

        val stack = Stack(stackStart, size)
        func(stack)

        cnt -= size
    }

    fun Stack.pop(v: Variable? = null) {

        if (v != null) {
            peek(v)
        }

        for (i in this.start until this.start + this.size - 1) {
            Variable(i) set Variable(i + 1)
        }
    }

    fun Stack.push(e: Expression) {
        for (i in this.start + this.size - 1 downTo this.start + 1) {
            Variable(i) set Variable(i - 1)
        }
        Variable(this.start) set e
    }

    fun Stack.peek(v: Variable) {
        v set Variable(this.start)
    }

    fun Stack.set(e: Expression) {
        Variable(this.start) set e
    }

    fun Stack.push(e: Char) {
        this.push(Constant(e.toInt()))
    }


    fun useArray(size: Int, func: BrainFuckMachine.(BfArray) -> Unit) {
        declare { currentIndex ->
            useStack(size) { stack1 ->
                useStack(size) { stack2 ->
                    val stack = BfArray(size, currentIndex.index, stack1, stack2)
                    func(stack)
                }
            }
        }
    }


    fun arrayAccess(arr: BfArray, index: Expression, func: (s1: Stack) -> Unit) {
        val indexValue = evaluate(index)
        blockRegister(indexValue) {

            val s1 = arr.stack1
            val s2 = arr.stack2

            declare { r1v ->
                //write("before")
                //writeCellAsNumber(indexValue)
                //write("\n")


                condition(indexValue gt Variable(arr.currentIndexVar), {
                    declare { needShift ->
                        needShift set (indexValue - Variable(arr.currentIndexVar))
                        timesLoop(needShift) {
                            s1.pop(r1v)
                            s2.push(r1v)
                        }
                    }
                }, {
                    declare { needShift ->
                        needShift set (Variable(arr.currentIndexVar) - indexValue)
                        timesLoop(needShift) {
                            s2.pop(r1v)
                            s1.push(r1v)
                        }
                    }
                })


                Variable(arr.currentIndexVar) set indexValue

                func(s1)

                //write("after")
                //writeCellAsNumber(indexValue)
                //write("\n")

            }

        }

    }

    fun timesLoop(v: Variable, func: BrainFuckMachine.() -> Unit) {
        declare { iVar ->

            iVar set v

            whileLoop(iVar gt 0) {
                func()
                iVar set (iVar - 1)
            }

        }
    }


    operator fun BfArray.set(index: Expression, value: Expression) {
        arrayAccess(this, index) { stack ->
            stack.set(value)
        }
    }

    inline operator fun BfArray.set(index: Expression, value: Char) {
        this.set(index, value.toInt())
    }

    inline operator fun BfArray.set(index: Int, value: Expression) {
        this.set(Constant(index), value)
    }

    inline operator fun BfArray.set(index: Expression, value: Int) {
        this.set(index, Constant(value))
    }

    operator fun BfArray.get(index: Expression): Expression {
        return ArrayAccess(this, index)
    }

    inline operator fun BfArray.get(index: Int): Expression {
        return this[Constant(index)]
    }

    fun forLoop(
        init: BrainFuckMachine.() -> Unit,
        condition: BrainFuckMachine.() -> Expression,
        operation: BrainFuckMachine.() -> Unit,
        body: BrainFuckMachine.() -> Unit
    ) {
        init()
        whileLoop(condition()) {
            body()
            operation()
        }
    }

    infix fun Variable.and(second: Variable): Expression {
        return Formula(ExpressionType.And, this, second)
    }

    inline fun declare(crossinline func: BrainFuckMachine.(Variable, Variable) -> Unit) {
        declare { v1 ->
            declare { v2 ->
                func(v1, v2)
            }
        }
    }


    inline fun declare(crossinline func: BrainFuckMachine.(Variable, Variable, Variable) -> Unit) {
        declare { v1 ->
            declare { v2 ->
                declare { v3 ->
                    func(v1, v2, v3)
                }
            }
        }
    }

    inline fun declare(crossinline func: BrainFuckMachine.(Variable, Variable, Variable, Variable) -> Unit) {
        declare { v1 ->
            declare { v2 ->
                declare { v3 ->
                    declare { v4 ->
                        func(v1, v2, v3, v4)
                    }
                }
            }
        }
    }


    infix fun Expression.and(second: Expression) =
        Formula(ExpressionType.And, this, second)

    fun not(variable: Expression) = Formula(
        ExpressionType.Eq,
        variable,
        Constant(0)
    )

    infix fun Expression.or(variable: Expression) = not(not(this) and not(variable))
    operator fun Expression.plus(second: Expression) =
        Formula(ExpressionType.Plus, this, second)

    operator fun Expression.minus(second: Expression) =
        Formula(ExpressionType.Minus, this, second)

    operator fun Expression.minus(second: Int) = this.minus(
        Constant(
            second
        )
    )

    operator fun Expression.minus(second: Char) = this.minus(
        Constant(
            second.toInt()
        )
    )


    operator fun Expression.times(second: Expression) =
        Formula(ExpressionType.Mult, this, second)

    operator fun Expression.times(second: Int) = this.times(
        Constant(
            second
        )
    )

    fun write(v: Expression) = bf.write(evaluate(v).index)
    fun writeln(v: Expression) {
        write(v)
        write("\n")
    }

    fun create(func: BrainFuckMachine.() -> Unit) = func()

    override fun toString() = bf.toString()

    fun <T> blockRegister(r: Variable, func: () -> T): T {
        return blockRegister(r.index) {
            func()
        }
    }

    fun <T> blockRegister(r: Int, func: () -> T): T {
        var t: T;
        if (registerList.contains(r) && !usedRegister.contains(r)) {

            usedRegister.add(r)
            t = func()
            usedRegister.remove(r)
        } else {
            t = func()
        }
        return t;
    }

    fun Variable.times(func: (Variable) -> Unit) {
        blockRegister(this.index) {
            val variable = this
            bf.decLoop(this.index) {
                func(variable)
            }
        }
    }

    fun writeCellAsNumber(e: Expression) {

        declare { input, d, h ->
            input set e
            d set (input div 100)
            condition(d neq 0) {
                write(d + '0')
            }
            h set (input - (d * 100))

            d set (h div 10)
            condition(d neq 0) {
                write(d + '0')
            }
            d set (h - (d * 10))

            write(d + '0')

        }
    }


    infix fun Expression.eq(var2: Expression) =
        Formula(ExpressionType.Eq, this, var2)

    infix fun Expression.eq(value: Int) = this.eq(
        Constant(
            value
        )
    )

    infix fun Expression.neq(var2: Expression) =
        Formula(ExpressionType.Neq, this, var2)

    infix fun Expression.neq(var2: Int) = this.neq(
        Constant(
            var2
        )
    )

    infix fun Expression.gt(var2: Int) = this.gt(Constant(var2))

    infix fun Expression.gt(var2: Expression) =
        Formula(ExpressionType.Gt, this, var2)

    fun evaluate(expr: Expression): Variable {
        return when (expr) {
            is Variable -> expr
            is Constant -> {
                val reg = getFreeRegister()
                val variable = Variable(reg)
                bf.set(reg, expr.value)
                variable
            }

            is Formula -> {
                val var1 = evaluate(expr.v1)
                blockRegister(var1) {
                    val var2 = evaluate(expr.v2)
                    evaluateFormula(expr.type, var1, var2)
                }
            }

            is SingleVariableFormula -> {
                val var1 = evaluate(expr.e)
                val var2 = blockRegister(var1) {
                    evaluateFormula(expr.type, var1)
                }
                var2

            }
            is ArrayAccess -> {
                val reg = getFreeRegister()
                blockRegister(reg) {
                    val indexVar = evaluate(expr.index)

                    arrayAccess(expr.arr, indexVar) { stack ->
                        stack.peek(Variable(reg))
                    }

                }
                Variable(reg)
            }
            else -> TODO("")
        }
    }


    private fun evaluateFormula(
        type: SingleExpressionType,
        var1: Variable

    ): Variable {
        when (type) {
            else -> TODO("")
        }
    }


    private fun evaluateFormula(
        type: ExpressionType,
        var1: Variable,
        var2: Variable
    ): Variable {
        return when (type) {
            ExpressionType.Allocate -> TODO()
            ExpressionType.And -> {
                val (target, tmp1, tmp2) = getFreeRegister3(var1.index, var2.index)
                bf.assign(target, var1.index, tmp1)
                bf.and(target, var2.index, tmp1, tmp2)
                Variable(target)
            }
            ExpressionType.Plus -> {
                val (target, tmp) = getFreeRegister2(var1.index, var2.index)
                bf.assign(target, var1.index, tmp)
                bf.add(target, var2.index, tmp)
                Variable(target)
            }
            ExpressionType.Minus -> {
                val (target, tmp) = getFreeRegister2(var1.index, var2.index)
                bf.assign(target, var1.index, tmp)
                bf.sub(target, var2.index, tmp)
                Variable(target)
            }
            ExpressionType.Mult -> {
                val (target, tmp, tmp2) = getFreeRegister3(var1.index, var2.index)
                bf.assign(target, var1.index, tmp)
                bf.mult(target, var2.index, tmp, tmp2)
                Variable(target)
            }
            ExpressionType.Div -> {
                val (target, tmp, tmp2, tmp3, tmp4) = getFreeRegister5(var1.index, var2.index)
                bf.assign(target, var1.index, tmp)
                bf.div(target, var2.index, tmp, tmp2, tmp3, tmp4)
                Variable(target)


            }
            ExpressionType.Neq -> {
                val (target, tmp, tmp2) = getFreeRegister3(var1.index, var2.index)
                bf.assign(target, var1.index, tmp)
                bf.neq(target, var2.index, tmp, tmp2)
                Variable(target)
            }

            ExpressionType.Eq -> {
                val (target, tmp, tmp2) = getFreeRegister3(var1.index, var2.index)
                bf.assign(target, var1.index, tmp)
                bf.eq(target, var2.index, tmp, tmp2)
                Variable(target)
            }
            ExpressionType.Gt -> {
                val (target, o1, o2, tmp3, tmp4) = getFreeRegister5(var1.index, var2.index)
                bf.assign(o1, var1.index, tmp3)
                bf.assign(o2, var2.index, tmp3)
                bf.gt(target, o1, o2, tmp3, tmp4)
                Variable(target)
            }
        }
    }

    fun write(v: String) {
        declare { variable ->
            bf.program {
                for (c in v) {

                    val intVal = c.toInt()

                    move(variable.index) {
                        inc(intVal)
                        write()
                        dec(intVal)
                    }
                }
            }

        }

    }

    fun whileLoop(expr: Expression, body: BrainFuckMachine.() -> Unit) = whileLoop(expr, body, {})

    infix fun Expression.swap(second: Expression) {
        val var1 = evaluate(this)
        blockRegister(var1) {
            val var2 = evaluate(second)
            val (tmp0, tmp1) = getFreeRegister2()
            bf.swap(var1.index, var2.index, tmp0, tmp1)
        }
    }

    fun whileLoop(expr: Expression, body: BrainFuckMachine.() -> Unit, negBody: BrainFuckMachine.() -> Unit) {
        val condVariable = evaluate(expr)
        blockRegister(condVariable) {

            condition(condVariable, {
                bf.program {
                    moveTo(0, condVariable.index)
                    loop {
                        moveTo(condVariable.index, 0)
                        body()
                        set(condVariable, expr)
                        moveTo(0, condVariable.index)
                    }
                    moveTo(condVariable.index, 0)
                }
            }, {
                negBody()
            })
        }
    }

    val TRUE: Expression
        get() = Constant(1)

    val FALSE: Expression
        get() = Constant(0)

    fun switch(expr: Expression, vararg cases: Pair<Any, () -> Unit>) {


        cases.forEach { case ->
            val value = case.first
            if (!(value is Int || value is Char || value is Expression)) {
                throw IllegalArgumentException("Value must be Int or Char or Expression")
            }
        }


        declare { mainVar ->
            mainVar set expr

            fun generate(index: Int) {

                if (index >= cases.size) {
                    return
                }

                val case = cases[index]
                val toCompare = when (val key = case.first) {
                    is Char -> Constant(key.toInt())
                    is Int -> Constant(key)
                    is Expression -> key
                    else -> TODO()
                }

                if (mainVar == toCompare) {
                    case.second()
                } else {
                    condition(mainVar eq toCompare, {
                        case.second()
                    }, {
                        generate(index + 1)
                    })
                }

            }


            generate(0)

        }
    }

    fun condition(expr: Expression, positive: BrainFuckMachine.() -> Unit) {
        condition(expr, positive, {})
    }

    fun condition(expr: Expression, positive: BrainFuckMachine.() -> Unit, negative: BrainFuckMachine.() -> Unit) {

        if (expr == TRUE) {
            positive()
            return
        } else if (expr == FALSE) {
            negative()
            return;
        }


        val (tmp0, tmp1) = getFreeRegister2()
        blockRegister(tmp0) {
            blockRegister(tmp1) {
                bf.program {
                    val variable = evaluate(expr)

                    val x = variable.index
                    blockRegister(x) {
                        reset(tmp0)
                        reset(tmp1)

                        decLoop(x) {
                            inc(tmp0, 1)
                            inc(tmp1, 1)
                        }

                        decLoop(tmp0) {
                            inc(x, 1)
                        }
                        inc(tmp0, 1)


                        // true branch
                        moveTo(0, tmp1)
                        loop {

                            moveTo(tmp1, 0)
                            positive()
                            moveTo(0, tmp1)

                            moveTo(tmp1, tmp0)
                            dec()
                            moveTo(tmp0, tmp1)
                            reset()
                        }

                        moveTo(tmp1, 0)

                        decLoop(tmp0) {
                            negative()
                        }
                    }
                }
            }
        }
    }
}