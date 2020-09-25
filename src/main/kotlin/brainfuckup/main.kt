package brainfuckup

import brainfuckup.interpret.BrainFuckCode
import brainfuckup.interpret.BrainFuckCodeInterpreter


fun main() {

    val bf = BrainFuckMachine()
    bf.create {
        declare { v1, end ->
            end set 0
            write("Guess my number. It is between 1 and 100. Write your guess\n")

            whileLoop(end eq 0) {
                declare { inputNumber, inputChar ->
                    read(inputChar)
                    whileLoop((inputChar neq 13) and (inputChar neq 10)) {
                        condition(
                            (inputChar gte '0') and (inputChar lte '9')
                        ) {
                            declare { filtered ->
                                filtered set (inputChar - '0')
                                inputNumber set ((inputNumber * 10) + filtered)
                            }
                        }
                        read(inputChar)
                    }
                    write("So ")
                    writeCellAsNumber(inputNumber)
                    write(" you say?\n")

                    condition(inputNumber eq 36, {
                        write("You won!\n")
                        end set 1
                    }, {
                        condition(inputNumber eq 1, {
                            write("Zbynek is number one!!! But it is not correct answer. Try once more\n")
                        }, {
                            condition(inputNumber gt 36, {
                                write("Your guess is too high :\\ You can try again\n")
                            }, {
                                write("Your guess is too low :\\\n You can try again\n")
                            })
                        })
                    })
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
    val inter = BrainFuckCodeInterpreter()
    println("Run :\n")
    inter.run(program)
}