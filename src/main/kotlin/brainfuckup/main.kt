package brainfuckup

import brainfuckup.interpret.BrainFuckCode
import brainfuckup.interpret.BrainFuckCodeInterpreter


fun main() {

    val bf = BrainFuckMachine()
    bf.create {
        declare { numberToGuess ->
            numberToGuess set 20 + 30
            numberToGuess set (numberToGuess div 2) + 5
            declare { end ->
                end set FALSE
                writeln("Guess my number. It is between 1 and 100. Write your guess")

                whileLoop(end eq FALSE) {
                    declare { inputNumber, success ->
                        readNumber(inputNumber, success)
                        condition(
                            expr = success,
                            then = {
                                write("So ")
                                writeCellAsNumber(inputNumber)
                                writeln(" you say?")
                                condition(inputNumber eq numberToGuess, {
                                    writeln("You won!")
                                    end set TRUE
                                }, {
                                    condition(inputNumber eq 1, {
                                        writeln("Zbynek is number one!!! But it is not correct answer. Try once more")
                                    }, {
                                        condition(inputNumber gt numberToGuess, {
                                            writeln("Your guess is too high :\\ You can try again")
                                        }, {
                                            writeln("Your guess is too low :\\\n You can try again")
                                        })
                                    })
                                })
                            },
                            els = {
                                writeln("Invalid number")
                            })

                    }
                }
            }
        }
    }
    println("Program:\n")
    val program = bf.toString()
    val code = BrainFuckCode()
    code.load(program)
    println(code)
    println(program)
    println("Program size: ${program.length}")
    val inter = BrainFuckCodeInterpreter()
    println("Run :\n")
    inter.run(program)
}