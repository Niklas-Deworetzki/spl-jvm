package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor

/**
 * This class represents an assignment in SPL.
 *
 * An assignment evaluates its right hand side expression and stores this value inside the variable
 * on the left hand side of the assignment operator.
 *
 * @param position The position of the statement in the source code.
 * @param target   The variable where the value is assigned to.
 * @param value    The value to be assigned.
 */
class AssignStatement(position: Position, val target: Variable, val value: Expression) : Statement(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("AssignStatement", target, value)
}
