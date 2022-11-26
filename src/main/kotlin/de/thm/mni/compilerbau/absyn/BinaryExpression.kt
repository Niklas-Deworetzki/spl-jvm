package de.thm.mni.compilerbau.absyn

/**
 * This class represents an expression, combining two expressions with an operator.
 * Example: 3 * i
 *
 * Binary expressions always combine two expressions of the type integer with one of 10 possible operators.
 * The operator defines, how the left and the right expression are combined.
 * The semantic type of an expression is dependant of the operator.
 *
 * @param lhs  The operand on the left-hand side of the operator.
 * @param operator     The operator used in this expression.
 * @param rhs The operand on the right-hand side of the operator.
 */
class BinaryExpression(val lhs: Expression, val operator: Operator, val rhs: Expression) :
    Expression() {

    enum class Operator(val representation: String) {
        ADD("+"),
        SUB("-"),
        MUL("*"),
        DIV("/"),
        EQU("="),
        NEQ("#"),
        LST("<"),
        LSE("<="),
        GRT(">"),
        GRE(">=");

        /**
         * Checks whether the operator is an arithmetic operator.
         *
         * @return true if the operator is an arithmetic operator.
         */
        fun isArithmetic(): Boolean {
            return when (this) {
                ADD, SUB, MUL, DIV -> true
                else -> false
            }
        }

        /**
         * Checks whether the operator is an equality operator (= and #).
         *
         * @return true if the operator is an equality operator.
         */
        open fun isEqualityOperator(): Boolean {
            return when (this) {
                NEQ, EQU -> true
                else -> false
            }
        }

        /**
         * Checks whether the operator is a comparison operator.
         *
         * @return true if the operator is a comparison operator.
         */
        open fun isComparison(): Boolean {
            return isEqualityOperator() || when (this) {
                LSE, LST, GRT, GRE -> true
                else -> false
            }
        }
    }

    override fun toString(): String = formatAst("BinaryExpression", operator, lhs, rhs)
}
