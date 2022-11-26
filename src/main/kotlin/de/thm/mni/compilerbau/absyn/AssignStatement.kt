package de.thm.mni.compilerbau.absyn

/**
 * This class represents an assignment in SPL.
 *
 * An assignment evaluates its right hand side expression and stores this value inside the variable
 * on the left hand side of the assignment operator.
 *
 * @param target   The variable where the value is assigned to.
 * @param value    The value to be assigned.
 */
class AssignStatement(val target: Variable, val value: Expression) : Statement() {
    override fun toString(): String = formatAst("AssignStatement", target, value)
}
