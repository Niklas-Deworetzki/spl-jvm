package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor

/**
 * This class represents a literal in SPL.
 * Examples: 12, 0x47 or 'a'
 *
 * Every time a number is used in an SPL program (decimal, hexadecimal or a character), this number is represented as
 * an integer. This numbers in the source code are called literals.
 *
 * @param position The position of the literal in the source code.
 * @param value    The value the literal holds.
 */
class IntLiteral(position: Position, val value: Int) : Expression(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("IntLiteral", value)
}
