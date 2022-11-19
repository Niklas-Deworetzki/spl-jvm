package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor

/**
 * This class represents an if-statement in SPL.
 *
 * An if-statement consists of two branches and an expression as condition. Which of the two branches
 * is executed depends on the value of the boolean-typed condition.
 *
 * @param position  The position of the statement in the source code.
 * @param condition The expression deciding which branch to execute.
 * @param thenPart  The executed statement if the expression evaluates to true.
 * @param elsePart  The executed statement if the expression evaluates to false.
 */
class IfStatement(position: Position, val condition: Expression, val thenPart: Statement, val elsePart: Statement) : Statement(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("IfStatement", condition, thenPart, elsePart)
}
