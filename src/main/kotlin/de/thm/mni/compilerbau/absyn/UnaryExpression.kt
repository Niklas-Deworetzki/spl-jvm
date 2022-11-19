package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor

/**
 * This class represents an expression with a unary operator.
 * Example: -x, !(a < b)
 * <p>
 * Unary expressions apply a unary operator to a single operand on the right side of the operator.
 * The type of the expression depends on the operator.
 *
 * @param position     The position of the expression in the source code.
 * @param operator     The operator used in this expression.
 * @param operand      The operand on the right hand side of the operator.
 */
class UnaryExpression(
    position: Position,
    val operator: Operator,
    val operand: Expression
) : Expression(position) {
    enum class Operator {
        MINUS;

        fun operatorString(): String {
            return when (this) {
                MINUS -> "-"
            }
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("UnaryExpression", operator, operand)
}
