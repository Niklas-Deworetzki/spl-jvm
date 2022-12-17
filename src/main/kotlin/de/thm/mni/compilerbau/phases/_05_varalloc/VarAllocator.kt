package de.thm.mni.compilerbau.phases._05_varalloc

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.jvm.InternalType
import de.thm.mni.compilerbau.phases._05_varalloc.AllocationPrettyPrinter.formatVars
import de.thm.mni.compilerbau.table.ParameterType
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.types.ArrayType
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

        val arguments = mutableListOf<InternalType>()
        for (parameter in declaration.parameters) {
            val localVariable = entry.localTable.lookupAs<VariableEntry>(parameter.name)
            localVariable.offset = arguments.size
            arguments.add(InternalType.of(localVariable))
        }
        val variables = mutableListOf<InternalType>()
        for (variable in declaration.variables) {
            val localVariable = entry.localTable.lookupAs<VariableEntry>(variable.name)
            localVariable.offset = arguments.size + variables.size
            variables.add(InternalType.of(localVariable))
        }

        entry.stackLayout = StackLayout(arguments, variables, referencePoolSize)
    }

    private class ArgumentCalculator(val declaration: ProcedureDeclaration, val scope: SymbolTable) {
        private var simultaneousArgumentsRequiringPromotion: Int = 0

        fun computeReferencePoolSize(): Int {
            declaration.forEachStatement(::evalStatement)
            return simultaneousArgumentsRequiringPromotion
        }

        private fun evalStatement(statement: Statement) {
            if (statement is CallStatement) {
                val calledProcedure = scope.upperLevel?.lookupAs<ProcedureEntry>(statement.procedureName)!!

                var requiredPromotions = 0
                for (index in statement.arguments.indices) {
                    val parameterIsReference = calledProcedure.parameterTypes[index].isReference
                    val argument = statement.arguments[index]

                    argument.passingMode = when {
                        parameterIsReference && argumentRequiresPromotion(argument) ->
                            Argument.PromoteToReference(requiredPromotions++)

                        parameterIsReference && argument.value.dataType is ArrayType ->
                            Argument.ByReferenceArray

                        parameterIsReference ->
                            Argument.ByReferenceInteger

                        else ->
                            Argument.ByValue
                    }
                }
                this.simultaneousArgumentsRequiringPromotion =
                    max(this.simultaneousArgumentsRequiringPromotion, requiredPromotions)
            }
        }

        private fun argumentRequiresPromotion(argument: Argument): Boolean {
            val variable = argument.asVariable()
            return variable is NamedVariable && !scope.lookupAs<VariableEntry>(variable.name).isReference
        }
    }
}