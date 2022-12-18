package de.thm.mni.compilerbau.absyn

/**
 * This class represents an expression with a unary operator.
 * Example: -x, !(a < b)
 * <p>
 * Unary expressions apply a unary operator to a single operand on the right side of the operator.
 * The type of the expression depends on the operator.
 *
 * @param operator     The operator used in this expression.
 * @param operand      The operand on the right hand side of the operator.
 */
class UnaryExpression(
    val operator: Operator,
    val operand: Expression
) : Expression() {

    enum class Operator(val representation: String) {
        MINUS("-")
    }

    override fun toString(): String = formatAst("UnaryExpression", operator.representation, operand)
}
