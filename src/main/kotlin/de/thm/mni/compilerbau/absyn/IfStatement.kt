package de.thm.mni.compilerbau.absyn

/**
 * This class represents an if-statement in SPL.
 *
 * An if-statement consists of two branches and an expression as condition. Which of the two branches
 * is executed depends on the value of the boolean-typed condition.
 *
 * @param condition The expression deciding which branch to execute.
 * @param thenPart  The executed statement if the expression evaluates to true.
 * @param elsePart  The executed statement if the expression evaluates to false.
 */
class IfStatement(val condition: Expression, val thenPart: Statement, val elsePart: Statement) : Statement() {
    override fun toString(): String = formatAst("IfStatement", condition, thenPart, elsePart)
}
