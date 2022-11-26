package de.thm.mni.compilerbau.absyn

/**
 * This class represents a literal in SPL.
 * Examples: 12, 0x47 or 'a'
 *
 * Every time a number is used in an SPL program (decimal, hexadecimal or a character), this number is represented as
 * an integer. This numbers in the source code are called literals.
 *
 * @param value    The value the literal holds.
 */
class IntLiteral(val value: Int) : Expression() {
    override fun toString(): String = formatAst("IntLiteral", value)
}
