package de.thm.mni.compilerbau.absyn

/**
 * This class represents a variable that is used as an [Expression].
 *
 * Example: 3 * i<br></br>
 * In this example, the named variable 'i' is used as the right operand of the arithmetic expression.
 * When using a [VariableExpression], the value that a variable holds is requested instead of its address.
 * The semantic type of a [VariableExpression] is the same as the type of its contained [Variable].
 *
 * @param variable The variable whose value is used as a value for this expression.
 */
class VariableExpression(val variable: Variable) : Expression() {
    override fun toString(): String = formatAst("VariableExpression", variable)
}
