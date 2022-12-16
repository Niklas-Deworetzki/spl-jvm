package de.thm.mni.compilerbau.phases._05_varalloc

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.phases._05_varalloc.AllocationPrettyPrinter.formatVars
import de.thm.mni.compilerbau.table.ParameterType
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.utils.ExtendedSyntax.asVariable
import de.thm.mni.compilerbau.utils.ExtendedSyntax.forEachStatement
import kotlin.math.max

/**
 * This class is used to calculate the memory needed for variables and stack frames of the currently compiled SPL program.
 * Those value have to be stored in their corresponding fields in the [ProcedureEntry], [VariableEntry] and
 * [ParameterType] classes.
 *
 * @param options The options passed to the compiler
 */
class VarAllocator(private val options: CommandLineOptions) {

    fun allocVars(program: Program, table: SymbolTable) {
        for (procedure in program.declarations.filterIsInstance<ProcedureDeclaration>()) {
            computeStackLayout(procedure, table.lookup(procedure.name)!! as ProcedureEntry)
        }

        if (options.debug == CommandLineOptions.DebugPhase.VARS)
            println(formatVars(program, table))
    }

    private fun computeStackLayout(declaration: ProcedureDeclaration, entry: ProcedureEntry) {
        val referencePoolSize = ArgumentCalculator(declaration, entry.localTable).computeReferencePoolSize()

        var currentStackOffset = 0
        for (parameter in declaration.parameters) {
            val localVariable = entry.localTable.lookupAs<VariableEntry>(parameter.name)
            localVariable.offset = currentStackOffset++
        }
        for (variable in declaration.variables) {
            val localVariable = entry.localTable.lookupAs<VariableEntry>(variable.name)
            localVariable.offset = currentStackOffset++
        }

        entry.stackLayout = StackLayout(
            declaration.parameters.size,
            declaration.variables.size,
            referencePoolSize
        )
    }

    private class ArgumentCalculator(val declaration: ProcedureDeclaration, val scope: SymbolTable) {
        private var simultaneousArgumentsRequiringPromotion: Int = 0

        fun computeReferencePoolSize(): Int {
            declaration.forEachStatement(::evalStatement)
            return simultaneousArgumentsRequiringPromotion
        }

        private fun evalStatement(statement: Statement) {
            if (statement is CallStatement) {
                val calledProcedure = scope.upperLevel?.lookup(statement.procedureName)!! as ProcedureEntry

                var requiredPromotions = 0
                for (index in statement.arguments.indices) {
                    if (argumentRequiresPromotion(statement, calledProcedure, index)) {
                        statement.arguments[index].promotion = Argument.Promote(requiredPromotions)
                        requiredPromotions += 1
                    } else {
                        statement.arguments[index].promotion = Argument.NoPromotion
                    }
                }
                this.simultaneousArgumentsRequiringPromotion =
                    max(this.simultaneousArgumentsRequiringPromotion, requiredPromotions)
            }
        }

        private fun argumentRequiresPromotion(
            call: CallStatement,
            calledProcedure: ProcedureEntry,
            index: Int
        ): Boolean {
            val parameterIsReference = calledProcedure.parameterTypes[index].isReference
            if (parameterIsReference) {
                val argument = call.arguments[index].asVariable()
                if (argument is NamedVariable && !scope.lookupAs<VariableEntry>(argument.name).isReference) {
                    return true
                }
            }
            return false
        }
    }
}