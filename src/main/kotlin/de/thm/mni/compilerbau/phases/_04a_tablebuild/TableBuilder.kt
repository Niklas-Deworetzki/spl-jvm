package de.thm.mni.compilerbau.phases._04a_tablebuild

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.phases.ErrorReport.Companion.quote
import de.thm.mni.compilerbau.phases.Pass
import de.thm.mni.compilerbau.table.*
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.Type

/**
 * This class is used to create and populate a [SymbolTable] containing entries for every symbol in the currently
 * compiled SPL program.
 * Every declaration of the SPL program needs its corresponding entry in the [SymbolTable].
 *
 * Calculated [Type]s can be stored in and read from the dataType field of the [Expression],
 * [TypeExpression] or [Variable] classes.
 */

class TableBuilder(private val options: CommandLineOptions) : Pass() {

    fun buildSymbolTable(program: Program): SymbolTable {
        val globalTable = TableInitializer.initializeGlobalTable(options)
        val typeComputation = TypeComputation(this, globalTable)

        for (declaration in program.declarations) {
            val entry = constructEntry(typeComputation, declaration)
            if (!globalTable.enter(declaration.name, entry)) {
                reportError(
                    declaration.position,
                    "Redeclaration of %s as %s",
                    declaration.name.quote(),
                    describeGlobalDeclarationVariant(declaration)
                )
            }
        }

        when (val mainEntry = globalTable.lookup(IDENTIFIER_MAIN)) {
            null ->
                reportError("Procedure %s is missing.", IDENTIFIER_MAIN.quote())

            is TypeEntry ->
                reportError("The program entry point %s is not a procedure.", IDENTIFIER_MAIN.quote())

            is ProcedureEntry ->
                if (mainEntry.parameterTypes.isNotEmpty())
                    reportError("The program entry point %s must not have any parameters.", IDENTIFIER_MAIN.quote())
        }

        return globalTable
    }

    private fun constructEntry(typeComputation: TypeComputation, declaration: GlobalDeclaration): Entry =
        when (declaration) {
            is TypeDeclaration ->
                TypeEntry(typeComputation.typeOf(declaration.typeExpression))

            is ProcedureDeclaration -> {
                val localScope = SymbolTable(typeComputation.scope)
                val parameters = mutableListOf<ParameterType>()

                for (parameter in declaration.parameters) {
                    val type = typeComputation.typeOf(parameter)
                    parameters.add(ParameterType(type, parameter.isReference))
                    enterLocal(localScope, parameter.name, VariableEntry(type, parameter.isReference), "parameter")

                    if (type is ArrayType && !parameter.isReference) {
                        reportError(parameter.position, "Array parameters must be passed by reference.")
                    }
                }

                for (variable in declaration.variables) {
                    val type = typeComputation.typeOf(variable)
                    enterLocal(localScope, variable.name, VariableEntry(type, false), "variable")
                }

                val result = ProcedureEntry(localScope, parameters)
                if (options.debug === CommandLineOptions.DebugPhase.TABLES) {
                    printSymbolTableAtEndOfProcedure(declaration.name, result)
                }
                result
            }
        }

    private fun enterLocal(scope: SymbolTable, name: Identifier, entry: Entry, what: String) {
        if (!scope.enter(name, entry)) reportError("Redeclaration of %s as %s", name.quote(), what)
    }

    private fun describeGlobalDeclarationVariant(globalDeclaration: GlobalDeclaration): String =
        when (globalDeclaration) {
            is ProcedureDeclaration -> "procedure"
            is TypeDeclaration -> "type"
        }

    companion object {
        val IDENTIFIER_MAIN = Identifier("main")

        /**
         * Prints the local symbol table of a procedure together with a heading-line
         * NOTE: You have to call this after completing the local table to support '--tables'.
         *
         * @param name  The name of the procedure
         * @param entry The entry of the procedure to print
         */
        private fun printSymbolTableAtEndOfProcedure(name: Identifier, entry: ProcedureEntry) {
            println("Symbol table at end of procedure '$name':")
            println(entry.localTable)
        }
    }
}
