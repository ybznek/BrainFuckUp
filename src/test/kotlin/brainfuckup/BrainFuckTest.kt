package brainfuckup

import brainfuckup.expression.Constant
import brainfuckup.expression.Expression
import brainfuckup.expression.Variable
import org.junit.Assert
import org.junit.Test
import java.util.*

class BrainFuckTest {

    val interpreter = TestInterpreterSimple()
    val machine = BrainFuckMachine()

    val mem: SortedMap<Int, Short>
        get() = interpreter.getMemoryDump()

    lateinit var program: String
    private fun run() {

        program = machine.toString()
        interpreter.run(program)
    }

    @Test
    fun testAnd_1_1() = testLogicOperator(1, 1, 1) { x, y -> x and y }

    @Test
    fun testAnd_1_0() = testLogicOperator(1, 0, 0) { x, y -> x and y }

    @Test
    fun testAnd_0_1() = testLogicOperator(0, 1, 0) { x, y -> x and y }

    @Test
    fun testAnd_0_0() = testLogicOperator(0, 0, 0) { x, y -> x and y }

    @Test
    fun testOr_1_1() = testLogicOperator(1, 1, 1) { x, y -> x or y }

    @Test
    fun testOr_1_0() = testLogicOperator(1, 0, 1) { x, y -> x or y }

    @Test
    fun testOr_0_1() = testLogicOperator(0, 1, 1) { x, y -> x or y }

    @Test
    fun testOr_0_0() = testLogicOperator(0, 0, 0) { x, y -> x or y }


    private fun testLogicOperator(
        v1: Int,
        v2: Int,
        v3: Int,
        func: BrainFuckMachine.(Variable, Variable) -> Expression
    ) {
        machine.create {
            Variable(1) set v1
            Variable(2) set v2
            set(Variable(3), func(Variable(1), Variable(2)))
        }

        run()
        Assert.assertEquals(v3.toShort(), mem[3])
    }

    @Test
    fun testSet() {

        machine.create {
            Variable(1) set 42
        }
        run()
        Assert.assertEquals(42.toShort(), mem[1])
    }

    @Test
    fun testForLoop() {
        machine.create {
            declare { i ->

                forLoop({ i set 10 }, { i lte 12 }, { i set i + 1 }) {
                    writeCellAsNumber(i)
                    write("\n")
                }

            }
        }
        run()
        Assert.assertEquals("10\n11\n12\n", interpreter.output)
    }

    @Test
    fun testRegister() {

        machine.create {
            Variable(1) set 10
            Variable(2) set 20
            Variable(3) set ((Variable(1) + 1) + (Variable(2) + 1))
        }
        run()
        Assert.assertEquals(32.toShort(), mem[3])
    }


    @Test
    fun testAdd() {

        machine.create {
            Variable(1) set 42
            Variable(2) set 43
            set(Variable(3), Variable(1) + Variable(2))
        }

        run()

        Assert.assertEquals(85.toShort(), mem[3])
    }


    @Test
    fun testSwitch() {
        var var3Index = 0
        machine.create {
            declare { var1, var3 ->
                var1 set 1
                var3Index = var3.index

                switch(var1,
                    1 to {
                        var3 set 10
                    },
                    2 to {
                        var3 set 20
                    },
                    3 to {
                        var3 set 30
                    }
                )
            }
        }

        run()

        Assert.assertEquals(10.toShort(), mem[var3Index])
    }

    @Test
    fun testSwitch2() {
        var var3Index = 0
        machine.create {
            declare { var1, var3 ->
                var1 set 2
                var3Index = var3.index

                switch(var1,
                    1 to {
                        var3 set 10
                    },
                    2 to {
                        var3 set 20
                    },
                    3 to {
                        var3 set 30
                    }
                )
            }
        }

        run()

        Assert.assertEquals(20.toShort(), mem[var3Index])
    }

    @Test
    fun testSwitch3() {
        var var3Index = 0
        machine.create {
            declare { var1, var3 ->
                var1 set 3
                var3Index = var3.index

                switch(var1,
                    1 to {
                        var3 set 10
                    },
                    2 to {
                        var3 set 20
                    },
                    3 to {
                        var3 set 30
                    }
                )
            }
        }

        run()

        Assert.assertEquals(30.toShort(), mem[var3Index])
    }


    @Test
    fun testStack() {

        machine.create {
            declare { variable ->
                useStack(3) { stack ->
                    declare { var2 ->
                        writeCellAsNumber(var2)
                        stack.push('A')
                        stack.push('B')
                        stack.push('C')

                        stack.pop(variable)
                        write(variable)

                        stack.pop(variable)
                        write(variable)

                        stack.pop(variable)
                        write(variable)


                        writeCellAsNumber(var2)
                    }
                }

            }
        }

        run()

        Assert.assertEquals("0CBA0", interpreter.output)
    }

    @Test
    fun testStack2() {

        machine.create {
            declare { variable ->
                useStack(3) { stack1 ->
                    useStack(3) { stack2 ->
                        declare { var2 ->
                            writeCellAsNumber(var2)
                            stack1.push('A')
                            stack1.push('B')
                            stack1.push('C')

                            stack1.pop(variable)
                            stack2.push(variable)

                            stack1.pop(variable)
                            stack2.push(variable)

                            stack1.pop(variable)
                            stack2.push(variable)

                            stack2.pop(variable)
                            write(variable)
                            stack2.pop(variable)
                            write(variable)
                            stack2.pop(variable)
                            write(variable)

                            writeCellAsNumber(var2)
                        }
                    }
                }
            }
        }

        run()

        Assert.assertEquals("0ABC0", interpreter.output)
    }

    @Test
    fun testSwitchElse() {

        var var3Index = 0
        machine.create {
            declare { var1, var3 ->
                var1 set 66
                var3Index = var3.index

                switch(var1,
                    1 to {
                        var3 set 10
                    },
                    2 to {
                        var3 set 20
                    },
                    3 to {
                        var3 set 30
                    },
                    var1 to {
                        var3 set 50
                    }
                )
            }
        }

        run()
        Assert.assertEquals(50.toShort(), mem[var3Index])
    }


    @Test
    fun testCondNegative() {

        machine.create {
            Variable(1) set 1
            Variable(2) set 2
            Variable(3) set 0

            condition(Variable(1) gt Variable(2), {
                Variable(3) set 42
            }, {
                Variable(3) set 3
            })
        }

        run()

        Assert.assertEquals(3.toShort(), mem[3])
    }


    @Test
    fun testCondPositive() {

        machine.create {
            Variable(1) set 1
            Variable(2) set 2
            Variable(3) set 0

            condition(Variable(2) gt Variable(1), {
                Variable(3) set 42
            }, {
                Variable(3) set 3
            })
        }

        run()

        Assert.assertEquals(42.toShort(), mem[3])
    }

    @Test
    fun testCondPComplex() {

        machine.create {
            Variable(1) set 1
            Variable(2) set 10
            Variable(3) set 0

            condition(Variable(2) gt 1, {
                Variable(3) set 42
            }, {
                condition(Variable(2) eq 5, {
                    Variable(3) set 3
                }, {
                    Variable(3) set 10
                })

            })
        }

        run()

        Assert.assertEquals(42.toShort(), mem[3])
    }


    @Test
    fun testNot1() {

        machine.create {
            Variable(1) set 1
            set(Variable(3), not(Variable(1)))
        }

        run()

        Assert.assertEquals(0.toShort(), mem[3])
    }

    @Test
    fun testNot0() {

        machine.create {
            Variable(1) set 0
            set(Variable(3), not(Variable(1)))
        }

        run()

        Assert.assertEquals(1.toShort(), mem[3])
    }


    @Test
    fun testSub() {

        machine.create {
            Variable(1) set 80
            Variable(2) set 60
            set(Variable(3), Variable(1) - Variable(2))
        }

        run()

        Assert.assertEquals(20.toShort(), mem[3])
    }


    @Test
    fun testGt_2_2() {

        machine.create {
            Variable(1) set 2
            Variable(2) set 2
            set(Variable(3), Variable(1) gt Variable(2))
        }

        run()

        Assert.assertEquals(0.toShort(), mem[3])
    }

    @Test
    fun testGt_2_3() {

        machine.create {
            Variable(1) set 2
            Variable(2) set 3
            set(Variable(3), Variable(1) gt Variable(2))
        }

        run()

        Assert.assertEquals(0.toShort(), mem[3])
    }

    @Test
    fun testGt_2_1() {

        machine.create {
            Variable(1) set 2
            Variable(2) set 1
            set(Variable(3), Variable(1) gt Variable(2))
        }

        run()

        Assert.assertEquals(1.toShort(), mem[3])
    }

    @Test
    fun testMult() {

        machine.create {

            Variable(1) set 2
            Variable(2) set 3
            set(Variable(3), Variable(1) * Variable(2))
        }

        run()

        Assert.assertEquals(6.toShort(), mem[3])
    }

    @Test
    fun testEqDifferent() {

        machine.create {

            Variable(1) set 42
            Variable(2) set 43
            Variable(3) set (Variable(1) eq Variable(2))
        }

        run()

        Assert.assertEquals(0.toShort(), mem[3])
    }

    @Test
    fun testEqSame() {

        machine.create {

            set(Variable(1), 42)
            set(Variable(2), 42)
            Variable(3) set (Variable(1) eq Variable(2))
        }

        run()

        Assert.assertEquals(1.toShort(), mem[3])
    }

    @Test
    fun testNeqDifferent() {

        machine.create {

            Variable(1) set 42
            Variable(2) set 43
            Variable(3) set (Variable(1) neq Variable(2))
        }

        run()

        Assert.assertEquals(1.toShort(), mem[3])
    }

    @Test
    fun testNeqSame() {

        machine.create {

            set(Variable(1), 42)
            set(Variable(2), 42)
            Variable(3) set (Variable(1) neq Variable(2))
        }

        run()

        Assert.assertEquals(0.toShort(), mem[3])
    }


    @Test
    fun testWhileLoop() {

        machine.create {
            declare { v1, v2 ->
                v1 set 10
                v2 set 0

                whileLoop(v1 gt 0) {
                    v1 set (v1 - 1)
                    v2 set (v2 + 1)
                }
            }

        }

        run()

        Assert.assertEquals(10.toShort(), mem[2])
    }

    @Test
    fun testSwap() {

        machine.create {
            declare { v1, v2 ->
                v1 set 1
                v2 set 2
                v1 swap v2
            }
        }

        run()

        Assert.assertEquals(2.toShort(), mem[1])
        Assert.assertEquals(1.toShort(), mem[2])
    }


    @Test
    fun testArray() {

        machine.create {
            declare { canary1 ->
                canary1 set 'A'.toInt()
                useArray(10) { array ->
                    declare { canary2 ->
                        canary2 set 'B'.toInt()
                        array[Constant(1)] = Constant('A'.toInt())
                        array[Constant(0)] = Constant('C'.toInt())
                        array[Constant(2)] = Constant('U'.toInt())

                        write(canary1)
                        write(array[Constant(0)])
                        write(array[Constant(1)])
                        write(array[Constant(2)])
                        write(canary2)

                    }
                }
            }
        }

        run()

        Assert.assertEquals("ACAUB", interpreter.output)
    }


    @Test
    fun testDiv() {

        machine.create {
            Variable(1) set 10
            Variable(2) set 3
            Variable(3) set ((Variable(1) div Variable(2)))
        }

        run()

        Assert.assertEquals(3.toShort(), mem[3])

    }


    @Test
    fun testDiv2() {

        machine.create {
            Variable(1) set 10
            Variable(2) set 2
            Variable(3) set ((Variable(1) div Variable(2)))
        }

        run()

        Assert.assertEquals(5.toShort(), mem[3])

    }


    @Test
    fun incrementOptimization_var_first() {
        machine.create {
            declare { var1 ->
                var1 set var1 + 3
            }
        }
        val generated = machine.toString()
        Assert.assertEquals(generated, ">[-]+++<")
    }

    @Test
    fun incrementOptimization_var_last() {
        machine.create {
            declare { var1 ->
                var1 set 3 + var1
            }
        }
        val generated = machine.toString()
        Assert.assertEquals(generated, ">[-]+++<")
    }


    @Test
    fun decrementOptimization() {
        machine.create {
            declare { var1 ->
                var1 set var1 - 3
            }
        }
        val generated = machine.toString()
        Assert.assertEquals(generated, ">[-]---<")
    }


}