package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor

/**
 * This class represents a while-statement in SPL.
 *
 * A while-statement consists of a condition expression and a body. The body is repeatedly executed until the condition's
 * value becomes false.
 *
 * @param position  The position of the statement in the source code.
 * @param condition The expression used to determine whether the while-loop should continue.
 * @param body      The statement executed until the condition evaluates to false.
 */
class WhileStatement(position: Position, val condition: Expression, val body: Statement) : Statement(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("WhileStatement", condition, body)
}
