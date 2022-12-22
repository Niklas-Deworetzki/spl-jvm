package de.thm.mni.compilerbau.phases._04b_semant

import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.phases.Pass
import de.thm.mni.compilerbau.reporting.Message.Companion.quoted
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.types.PrimitiveType
import de.thm.mni.compilerbau.types.Type
import de.thm.mni.compilerbau.utils.ExtendedSyntax.forEachStatement

/**
 * This class is used to check if the currently compiled SPL program is semantically valid.
 * The body of each procedure has to be checked, consisting of [Statement]s, [Variable]s and [Expression]s.
 * Each node has to be checked for type issues or other semantic issues.
 * Calculated [Type]s can be stored in and read from the dataType field of the [Expression] and [Variable] classes.
 */
object ProcedureBodyChecker : Pass() {

    fun checkProcedures(program: Program, globalTable: SymbolTable) {
        for (procedure in program.declarations.filterIsInstance<ProcedureDeclaration>()) {
            val entry = globalTable.lookup(procedure.name)!! as ProcedureEntry
            val checker = TypeChecker(this, entry.localTable)

            procedure.forEachStatement { verifyStatement(checker, it) }
        }
    }

    private fun verifyStatement(checker: TypeChecker, statement: Statement) {
        when (statement) {
            is AssignStatement -> {
                checker.verify(statement.target)
                checker.verify(statement.value)

                if (
                    statement.target.dataType !in setOf(PrimitiveType.Int, PrimitiveType.Bottom) ||
                    statement.value.dataType !in setOf(PrimitiveType.Int, PrimitiveType.Bottom)
                ) {
                    reportError(statement.position, "Both sides of an assignment must be an integer.")
                }
            }

            is IfStatement -> {
                checker.verify(statement.condition)
                if (statement.condition.dataType !in setOf(PrimitiveType.Bool, PrimitiveType.Bottom)) {
                    reportError(statement.condition.position, "Condition must be a boolean.")
                }
            }


            is WhileStatement -> {
                checker.verify(statement.condition)
                if (statement.condition.dataType !in setOf(PrimitiveType.Bool, PrimitiveType.Bottom)) {
                    reportError(statement.condition.position, "Condition must be a boolean.")
                }
            }


            is CallStatement -> {
                val targetEntry = checker.scope.upperLevel?.lookup(statement.procedureName)
                if (targetEntry !is ProcedureEntry) {
                    reportError(statement.position, "Undefined procedure %s.", statement.procedureName.quoted())
                    return
                }

                val parameterCount = targetEntry.parameterTypes.size
                val argumentCount = statement.arguments.size

                if (parameterCount == argumentCount) {
                    targetEntry.parameterTypes.zip(statement.arguments).forEach { (parameter, argument) ->
                        checker.verify(argument.value)
                        if (parameter.isReference && argument.value !is VariableExpression) {
                            reportError(argument.position, "Variable is required for reference parameters.")
                        }

                        val isCorrectArgumentType =
                            argument.value.dataType === PrimitiveType.Bottom || argument.value.dataType == parameter.type
                        if (!isCorrectArgumentType) {
                            reportError(argument.position, "Wrong type for argument.")
                        }
                    }

                } else if (parameterCount > argumentCount) {
                    reportError(statement.position, "Not enough arguments.")
                } else {
                    reportError(statement.position, "Too many arguments.")
                }
            }


            else ->
                Unit
        }
    }
}
