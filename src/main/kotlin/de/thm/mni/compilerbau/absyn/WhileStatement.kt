package de.thm.mni.compilerbau.absyn

/**
 * This class represents a while-statement in SPL.
 *
 * A while-statement consists of a condition expression and a body. The body is repeatedly executed until the condition's
 * value becomes false.
 *
 * @param condition The expression used to determine whether the while-loop should continue.
 * @param body      The statement executed until the condition evaluates to false.
 */
class WhileStatement(val condition: Expression, val body: Statement) : Statement() {
    override fun toString(): String = formatAst("WhileStatement", condition, body)
}
