package brainfuckup.expression

import brainfuckup.expression.Expression

class Constant(value:Int) : Expression {
    val value: Int = value

    constructor(value: Char) : this(value.toInt()) {
    }
}


