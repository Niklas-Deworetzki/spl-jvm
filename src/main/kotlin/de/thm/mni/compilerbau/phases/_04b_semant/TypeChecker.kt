package de.thm.mni.compilerbau.phases._04b_semant

import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.phases.ErrorReport.Companion.quote
import de.thm.mni.compilerbau.phases.Pass
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.PrimitiveType

class TypeChecker(private val pass: Pass, val scope: SymbolTable) {

    fun verify(variable: Variable): Unit = when (variable) {
        is NamedVariable ->
            when (val entry = scope.lookup(variable.name)) {
                is VariableEntry -> {
                    variable.dataType = entry.type
                }

                else -> {
                    pass.reportError(variable.position, "Unknown variable %s.", variable.name.quote())
                    variable.dataType = PrimitiveType.Bottom
                }
            }

        is ArrayAccess -> {
            verify(variable.array)
            verify(variable.index)

            if (variable.index.dataType !in setOf(PrimitiveType.Int, PrimitiveType.Bottom)) {
                pass.reportError(variable.index.position, "Only integers are valid array indices.")
            }


            when (variable.array.dataType) {
                is ArrayType -> {
                    val arrayType = variable.array.dataType as ArrayType
                    variable.dataType = arrayType.baseType
                }

                PrimitiveType.Bottom ->
                    Unit

                else -> {
                    pass.reportError(variable.array.position, "Only arrays can be accessed via index.")
                    variable.dataType = PrimitiveType.Bottom
                }
            }
        }
    }

    fun verify(expression: Expression): Unit = when (expression) {
        is BinaryExpression -> {
            verify(expression.lhs)
            if (expression.lhs.dataType !in setOf(PrimitiveType.Int, PrimitiveType.Bottom)) {
                pass.reportError(expression.lhs.position, "Binary operator requires integer operands.")
            }
            verify(expression.rhs)
            if (expression.rhs.dataType !in setOf(PrimitiveType.Int, PrimitiveType.Bottom)) {
                pass.reportError(expression.rhs.position, "Binary operator requires integer operands.")
            }

            expression.dataType = if (expression.operator.isComparison()) PrimitiveType.Bool else PrimitiveType.Int
        }

        is UnaryExpression -> {
            verify(expression.operand)
            if (expression.operand.dataType !in setOf(PrimitiveType.Int, PrimitiveType.Bottom)) {
                pass.reportError(expression.operand.position, "Unary operator requires integer operand.")
            }
            expression.dataType = PrimitiveType.Int
        }

        is VariableExpression -> {
            verify(expression.variable)
            expression.dataType = expression.variable.dataType
        }

        is IntLiteral ->
            expression.dataType = PrimitiveType.Int
    }

}