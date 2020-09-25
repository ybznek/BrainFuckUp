BrainFuckUp
===========

This project introduces Kotlin DSL for BrainFuck programs
- **BrainFuckCodeGenerator.kt**
    - wraps common patterns like sub, mult, add, ...
    - generate BrainFuck code a provides very basic optimization
- **BrainFuckMachine.kt**
    - Built above BrainFuckCodeGenerator, handle variable allocations and provide high-level interface
- **SimpleBrainFuckInterpreter.kt**
    - Simple BrainFuck interpreter
- **BrainFuckCodeInterpreter.kt**
    - BrainFuck intepreter using preprocessed source and interpret its own instruction set
Example (Number guessing game)
=======
Input:
```
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
```
Output:
```
>[-]>[-][-]>[-]+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++++++++++++++++++++++++++++++++++.----------------.++++++++++++++..-----------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++.-----------------------------------------------------------------------------------------.++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.+++++++.--------.-----------.+++.+++++++++++++.--------------------------------------------------------------------.--------------.+++++++++++++++++++++++++++++++++++++++++.+++++++++++++++++++++++++++++++++++++++++++.------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++.-----------------------------------------------------------------------------------.++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.+++.+++++++++++++++.+++.------------------..+++++++++.------------------------------------------------------------------------------.+++++++++++++++++.-----------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.+++++++++++++.----------.--------------------------------------------------------------------.+++++++++++++++++.-..--.--------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++.+++++++++++++++++++++++++++.---------.+++++++++++.---------------.---------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.----------.++++++.---.----------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++.----------------.++++++++++++++..---------------------------------------------------------------------------------------------------------.----------<<<<[-]<<[-]>[-]>>>>[<<<<+<+>>>>>-]<<<<<[>>>>>+<<<<<-][-]<[-]>>[<<+>>-]+>[<<<->+>>-]<<[>>+<<-]<[>>-<<[-]]>>>[-]<<[-]>[>+<<+>-]>[<+>-]+<<[>[>>>>>[-]>[-],<<<<<<<<[-]+++++++++++++<<[-]>[-]>>>>>>>>>[<<<<<<<<<+<+>>>>>>>>>>-]<<<<<<<<<<[>>>>>>>>>>+<<<<<<<<<<-][-]<[-]>>[<<+>>-]>[<<<->+>>-]<<[>>+<<-]<[>>+<<[-]]>>>[-]++++++++++<<<[-]>[-]>>>>>>>>>>[<<<<<<<<<<+<+>>>>>>>>>>>-]<<<<<<<<<<<[>>>>>>>>>>>+<<<<<<<<<<<-][-]<[-]>>[<<+>>-]>>[<<<<->+>>>-]<<<[>>>+<<<-]<[>>+<<[-]]>[-]>>>[-]<[>+<<<+>>-]<<[>>+<<-][-]<[-]>>>>[<<<<+>>>>-]<<<<[[-]>>[<<+>+>-]<[>+<-]<[>>>>+<<<<[-]]]>>>[-]<[-]>>[<+<+>>-]<[>+<-]+<[>>[<<<<<[-]++++++++++++++++++++++++++++++++++++++++++++++++<<<<[-]>>[-]>>>>>>>>>>>>>>>[<<<<<<<<<<<<<<<+<<+>>>>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<<<<[>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<<-][-]>[-]>>>[<<<+<+>>>>-]<<<<[>>>>+<<<<-][-]<[-]>>>>[-]<[<<+>[-<[-]<+>>]<[->>>+<<<]<[->>+<<]>>->-]>>[-]<<<[-]>[-]>[<+<+>>-]<<[>>+<<-][-]<[-]>>[<<+>>-]+>>[<<<<->+>>>-]<<<[>>>+<<<-]<[>>-<<[-]]>>>>[-]++++++++++++++++++++++++++++++++++++++++++++++++<<<[-]>>[-]>>>>>>>>>>>>>>[<<<<<<<<<<<<<<+<<+>>>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<<<[>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<-][-]<[-]>>>[<<<+>>>-]+>[<<<<->+>>>-]<<<[>>>+<<<-]<[>>>-<<<[-]]>>>>[-]<<<<[-]>[-]>>[<<+<+>>>-]<<<[>>>+<<<-][-]<[-]>>[<<+>>-]+>>>[<<<<<->+>>>>-]<<<<[>>>>+<<<<-]<[>>-<<[-]]>>>>[-]>[-]<<[>>+<+<-]>[<+>-][-]<<<[-]>>>>[<<<<+>>>>-]<<<<[[-]>[<+>>>+<<-]>>[<<+>>-]<<<[>>>>+<<<<[-]]]>>>[-]<<[-]>[-]>>[<<+<+>>>-]<<<[>>>+<<<-][-]<[-]>>[<<+>>-]+>[<<<->+>>-]<<[>>+<<-]<[>>-<<[-]]>>>>[-]+++++++++++++++++++++++++++++++++++++++++++++++++++++++++<<<<<[-]>>[-]>>>[<<<+<<+>>>>>-]<<<<<[>>>>>+<<<<<-][-]>[-]>>>>>>>>>>>>>>>>>[<<<<<<<<<<<<<<<<<+<+>>>>>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<<<<<[>>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<<<-][-]<[-]>>>>>[-]<<[<<+>[-<[-]<+>>]<[->>>>+<<<<]<[->>+<<]>>->-]>>>[-]<<<<[-]>[-]>>[<<+<+>>>-]<<<[>>>+<<<-][-]<[-]>>[<<+>>-]+>>>[<<<<<->+>>>>-]<<<<[>>>>+<<<<-]<[>>-<<[-]]>>>>>[-]+++++++++++++++++++++++++++++++++++++++++++++++++++++++++<<<<[-]>>>[-]>>>>>>>>>>>>>>[<<<<<<<<<<<<<<+<<<+>>>>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<<<<[>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<<-][-]<[-]>>>>[<<<<+>>>>-]+>[<<<<<->+>>>>-]<<<<[>>>>+<<<<-]<[>>>>-<<<<[-]]>>>>>[-]<<<<<[-]>[-]>>>[<<<+<+>>>>-]<<<<[>>>>+<<<<-][-]<[-]>>[<<+>>-]+>>>>[<<<<<<->+>>>>>-]<<<<<[>>>>>+<<<<<-]<[>>-<<[-]]>>>>>[-]>[-]<<<[>>>+<+<<-]>>[<<+>>-][-]<<<<[-]>>>>>[<<<<<+>>>>>-]<<<<<[[-]>[<+>>>>+<<<-]>>>[<<<+>>>-]<<<<[>>>>>+<<<<<[-]]]>>>>[-]<<<[-]>[-]>>>[<<<+<+>>>>-]<<<<[>>>>+<<<<-][-]<[-]>>[<<+>>-]+>>[<<<<->+>>>-]<<<[>>>+<<<-]<[>>-<<[-]]>>>>[-]>[-]<<[>>+<+<-]>[<+>-][-]<<<[-]>>>>[<<<<+>>>>-]<<<<[[-]>[<+>>>+<<-]>>[<<+>>-]<<<[>>>>+<<<<[-]]]>>>>>>[-]<[-]<[>>+<+<-]>>[<<+>>-]+<[>>>>>>>>>>>>>[-]<<<<<<<<<<<<<<[-]++++++++++++++++++++++++++++++++++++++++++++++++<<[-]>[-]>>>>>>>>>>>>>>[<<<<<<<<<<<<<<+<+>>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<<[>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<-][-]>>[<-<+>>-]<<[>>+<<-]>>[-]>>>>>>>>>>>>>>[-]<<<<<<<<<<<<<<<[>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<+<-]>[<+>-][-]++++++++++<<[-]>[-]>>>>>>>>>>>>>[<<<<<<<<<<<<<+<+>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<[>>>>>>>>>>>>>>+<<<<<<<<<<<<<<-][-]<[-]>>[<<+>>-]<<[>>>[<+<+>>-]<<[>>+<<-]<-]>[-]>>[-]<[>+<<+>-]<[>+<-][-]>>>>>>>>>>>>>>>>[<<<<<<<<<<<<<<+<<+>>>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<<<[>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<-]>[-]>>>>>>>>>>>>>[-]<<<<<<<<<<<<[>>>>>>>>>>>>+<<<<<<<<<<<<<+>-]<[>+<-]>>>-<[-]]>[-]>>>>>>>>>>>,<<<<<<<<<<<[-]+++++++++++++<<[-]>[-]>>>>>>>>>>>>[<<<<<<<<<<<<+<+>>>>>>>>>>>>>-]<<<<<<<<<<<<<[>>>>>>>>>>>>>+<<<<<<<<<<<<<-][-]<[-]>>[<<+>>-]>[<<<->+>>-]<<[>>+<<-]<[>>+<<[-]]>>>[-]++++++++++<<<[-]>[-]>>>>>>>>>>>>>[<<<<<<<<<<<<<+<+>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<[>>>>>>>>>>>>>>+<<<<<<<<<<<<<<-][-]<[-]>>[<<+>>-]>>[<<<<->+>>>-]<<<[>>>+<<<-]<[>>+<<[-]]>[-]>>>[-]<[>+<<<+>>-]<<[>>+<<-][-]<[-]>>>>[<<<<+>>>>-]<<<<[[-]>>[<<+>+>-]<[>+<-]<[>>>>+<<<<[-]]]>>>[-]>>>>[-]<<<[>>>+<<<<+>-]<[>+<-]>>>>]<-<[-]]>[-]>>>>>>>>>>[-]+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++++++++++++++++.-------------------------------------------------------------------------------.--------------------------------[-]>[-]>[-]<<<<<<<<<<<[-]>>>>>>>>>[-]<<[>>+<<<<<<<<<+>>>>>>>-]<<<<<<<[>>>>>>>+<<<<<<<-][-]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<<[-]>[-]>>>>>>>>>>[<<<<<<<<<<+<+>>>>>>>>>>>-]<<<<<<<<<<<[>>>>>>>>>>>+<<<<<<<<<<<-][-]<[-]<[-]<[-]>>>>[<+>-]<[>>[<<<+<+>>>>-]<<<<[>>>>+<<<<-]>[<+>>-[<<[-]<+>>>-]<<<[>>>+<<<-]>[>-[>>-<<[-]]+<-]>-]>>+<]>>[-]>>>>>>>>>>[-]<<<<<<<<<<<[>>>>>>>>>>>+<<<<<<<<<<+<-]>[<+>-]<<[-]<<[-]>[-]>>>>>>>>>>>>>[<<<<<<<<<<<<<+<+>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<[>>>>>>>>>>>>>>+<<<<<<<<<<<<<<-][-]<[-]>>[<<+>>-]>[<<<->+>>-]<<[>>+<<-]<[>>+<<[-]]>>>>>[-]<[-]<<[>>>+<+<<-]>>>[<<<+>>>-]+<[<[-]++++++++++++++++++++++++++++++++++++++++++++++++<<[-]>[-]>>>>>>>>>>>>>[<<<<<<<<<<<<<+<+>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<[>>>>>>>>>>>>>>+<<<<<<<<<<<<<<-][-]>>[<+<+>>-]<<[>>+<<-]>.>>>-<[-]]>[-][-]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<<[-]>[-]>>>>>>>>>>>[<<<<<<<<<<<+<+>>>>>>>>>>>>-]<<<<<<<<<<<<[>>>>>>>>>>>>+<<<<<<<<<<<<-][-]<[-]>>[<<+>>-]<<[>>>[<+<+>>-]<<[>>+<<-]<-]>[-]>>[-]>>>>>>>>>[<<<<<<<<<+<<+>>>>>>>>>>>-]<<<<<<<<<<<[>>>>>>>>>>>+<<<<<<<<<<<-][-]>[>-<<+>-]<[>+<-]>[-]>>>>>>>>>>>>[-]<<<<<<<<<<<[>>>>>>>>>>>+<<<<<<<<<<<<+>-]<[>+<-]>[-]++++++++++<<[-]>[-]>>>>>>>>>>>>[<<<<<<<<<<<<+<+>>>>>>>>>>>>>-]<<<<<<<<<<<<<[>>>>>>>>>>>>>+<<<<<<<<<<<<<-][-]<[-]<[-]<[-]>>>>[<+>-]<[>>[<<<+<+>>>>-]<<<<[>>>>+<<<<-]>[<+>>-[<<[-]<+>>>-]<<<[>>>+<<<-]>[>-[>>-<<[-]]+<-]>-]>>+<]>>[-]>>>>>>>>>>[-]<<<<<<<<<<<[>>>>>>>>>>>+<<<<<<<<<<+<-]>[<+>-]<<[-]<<[-]>[-]>>>>>>>>>>>>>[<<<<<<<<<<<<<+<+>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<[>>>>>>>>>>>>>>+<<<<<<<<<<<<<<-][-]<[-]>>[<<+>>-]>[<<<->+>>-]<<[>>+<<-]<[>>+<<[-]]>>>>>[-]<[-]<<[>>>+<+<<-]>>>[<<<+>>>-]+<[<[-]++++++++++++++++++++++++++++++++++++++++++++++++<<[-]>[-]>>>>>>>>>>>>>[<<<<<<<<<<<<<+<+>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<[>>>>>>>>>>>>>>+<<<<<<<<<<<<<<-][-]>>[<+<+>>-]<<[>>+<<-]>.>>>-<[-]]>[-][-]++++++++++<<[-]>[-]>>>>>>>>>>>[<<<<<<<<<<<+<+>>>>>>>>>>>>-]<<<<<<<<<<<<[>>>>>>>>>>>>+<<<<<<<<<<<<-][-]<[-]>>[<<+>>-]<<[>>>[<+<+>>-]<<[>>+<<-]<-]>[-]>>[-]>>>>>>>>>>>[<<<<<<<<<<<+<<+>>>>>>>>>>>>>-]<<<<<<<<<<<<<[>>>>>>>>>>>>>+<<<<<<<<<<<<<-][-]>[>-<<+>-]<[>+<-]>[-]>>>>>>>>>>>[-]<<<<<<<<<<[>>>>>>>>>>+<<<<<<<<<<<+>-]<[>+<-]>[-]++++++++++++++++++++++++++++++++++++++++++++++++<<[-]>[-]>>>>>>>>>>>[<<<<<<<<<<<+<+>>>>>>>>>>>>-]<<<<<<<<<<<<[>>>>>>>>>>>>+<<<<<<<<<<<<-][-]>>[<+<+>>-]<<[>>+<<-]>.>>>>>>>>>>[-]++++++++++++++++++++++++++++++++.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.----------.++++++.-------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.------------------.++++++++++++++++++++++++.----------------------------------------------------------.-----------------------------------------------------.----------<<<<<<<<<<<[-]++++++++++++++++++++++++++++++++++++<<[-]>[-]>>>>>>>>>>[<<<<<<<<<<+<+>>>>>>>>>>>-]<<<<<<<<<<<[>>>>>>>>>>>+<<<<<<<<<<<-][-]<[-]>>[<<+>>-]+>[<<<->+>>-]<<[>>+<<-]<[>>-<<[-]]>>>>>[-]<[-]<<[>>>+<+<<-]>>>[<<<+>>>-]+<[>>>>>>>>>>[-]+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++++++++++.++++++.-------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.--------.-.-----------------------------------------------------------------------------.-----------------------.----------<<<[-]+<<<<<<-<[-]]>[<<<<[-]+<<[-]>[-]>>>>>>>>>>>>[<<<<<<<<<<<<+<+>>>>>>>>>>>>>-]<<<<<<<<<<<<<[>>>>>>>>>>>>>+<<<<<<<<<<<<<-][-]<[-]>>[<<+>>-]+>[<<<->+>>-]<<[>>+<<-]<[>>-<<[-]]>>>>>[-]<[-]<<[>>>+<+<<-]>>>[<<<+>>>-]+<[>>>>>>>>>>>>[-]++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++.+++++++++++++++++++++++.-----------.---------.++++++.---------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++.-----------------------------------------------------------------------------------.++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.+++++++.--------.-----------.+++.+++++++++++++.----------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.-.---------.--------------------------------------------------------------------...-.++++++++++++++++++++++++++++++++++.+++++++++++++++++++++++++++++++++++++++++++++++++++.-.------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.+++++++++++.------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++.-----------------------------------------------------------------------------------.++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.+.+++++.------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++.+++..-------------.--.+++++++++++++++++.------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.+++++++++++++.+++++.++++.------------------.+++++++++++++.--------------------------------------------------------------------.--------------.++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++++++++++++++++++.+++++++.-----------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.-.-----------.++.---------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++.+++.-------------.-------------------------------------------------------------------------------------------.----------<<<<<<<<<<<-<[-]]>[<<<<[-]++++++++++++++++++++++++++++++++++++<<<<[-]>>[-]>>>>>>>>>>>>>>>[<<<<<<<<<<<<<<<+<<+>>>>>>>>>>>>>>>>>-]<<<<<<<<<<<<<<<<<[>>>>>>>>>>>>>>>>>+<<<<<<<<<<<<<<<<<-][-]>[-]>>>[<<<+<+>>>>-]<<<<[>>>>+<<<<-][-]<[-]>>>>[-]<[<<+>[-<[-]<+>>]<[->>>+<<<]<[->>+<<]>>->-]>>>>[-]<[-]<<[>>>+<+<<-]>>>[<<<+>>>-]+<[>>>>>>>>>>>>>>[-]+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++++++++++.++++++.---.----------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++.----------------.++++++++++++++..-----------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++.-----------------------------------------------------------------------------------.++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.-----..-------------------------------------------------------------------------------.++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.+.--.+.------------------------------------------------------------------------.++++++++++++++++++++++++++.++++++++++++++++++++++++++++++++++.------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++++++++++.++++++.-------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.--.+++++++++++++.------------------------------------------------------------------------------.++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.--.+++++++.-----------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++.------.++++++++.+++++.----------------------------------------------------------------------------------------------------.----------<<<<<<<<<<<<<-<[-]]>[>>>>>>>>>>>>>[-]+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++++++++++.++++++.---.----------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++.----------------.++++++++++++++..-----------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++.-----------------------------------------------------------------------------------.++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.-----..-------------------------------------------------------------------------------.++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.+++.++++++++.---------------------------------------------------------------------------------------.++++++++++++++++++++++++++.++++++++++++++++++++++++++++++++++.----------------------------------------------------------------------------------.++++++++++++++++++++++.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++++++++++++++++++.++++++.-------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.--.+++++++++++++.------------------------------------------------------------------------------.++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.--.+++++++.-----------------------------------------------------------------------------------------.+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++.++++++.------.++++++++.+++++.----------------------------------------------------------------------------------------------------.----------<<<<<<<<<<<<<-]>>-]>>-][-]<<[-]>[-]>>>>>>>[<<<<<<<+<+>>>>>>>>-]<<<<<<<<[>>>>>>>>+<<<<<<<<-][-]<[-]>>[<<+>>-]+>[<<<->+>>-]<<[>>+<<-]<[>>-<<[-]]>>>[-]>>[-]<<<[>>>+<<+<-]>[<+>-]>>]>-<<[-]]>>[-]>
```
  
TODO
====
- **array access by index variable**? (done, performance could be improved)
- **stack?** (done)
- **while loop with "break"**?
- **method call?**
- switch statement (done)
- refactoring
    - BrainFuckCodeGenerator#moveTo() usage is complicated
    - BrainFuckMachine - extract instructions to a separate file
    - ...
- improve performance of interpreter (merge multiple "+++" into one instruction, more effective memory storage, translate A = A + 1 to INC(A))

References
==========
- https://en.wikipedia.org/wiki/Brainfuck
- https://esolangs.org/wiki/Brainfuck_algorithms
