package de.thm.mni.compilerbau.phases._05_varalloc

import de.thm.mni.compilerbau.absyn.ProcedureDeclaration
import de.thm.mni.compilerbau.absyn.Program
import de.thm.mni.compilerbau.reporting.Message.Companion.quoted
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.table.SymbolTable

internal object AllocationPrettyPrinter {
    /**
     * Formats and prints the variable allocation to a human-readable format
     * The stack layout
     *
     * @param program The abstract syntax tree of the program
     * @param table   The symbol table containing all symbols of the spl program
     */
    fun formatVars(program: Program, table: SymbolTable) {
        for (procedure in program.declarations.filterIsInstance<ProcedureDeclaration>()) {
            val entry = table.lookup(procedure.name) as ProcedureEntry
            formatVarsProc(procedure, entry)
        }
    }

    private fun formatVarsProc(procedure: ProcedureDeclaration, entry: ProcedureEntry) {
        println("Stack Layout for Procedure ${procedure.name.quoted()}:")
        println("  Parameters: ${entry.stackLayout.argumentAreaSize}")
        println("  Local Variables: ${entry.stackLayout.localVariablesAreaSize}")
        println("  Pool for ref Arguments: ${entry.stackLayout.referencePoolSize}")
        println()
    }
}