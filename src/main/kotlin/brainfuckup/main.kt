package brainfuckup

import brainfuckup.expression.Constant
import brainfuckup.interpret.BrainFuckCode
import brainfuckup.interpret.BrainFuckCodeInterpreter


fun main() {

    val bf = BrainFuckMachine()
    bf.create {

        declare { i ->

            forLoop({ i set 10 }, { i lte 12 }, { i set i + 1 }) {
                writeCellAsNumber(i)
                write("\n")
            }

        }
    }

    println("Program:\n")
    val program = bf.toString()
    val code = BrainFuckCode()
    code.load(program)
    println(code)
    println(program)
    val inter = BrainFuckCodeInterpreter()
    println("Run :\n")
    inter.run(program)
    val mmm = inter.memory.entries.sortedBy { x -> x.key }
    var debug = 42;
}