package de.thm.mni.compilerbau.phases._05_varalloc

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.Program
import de.thm.mni.compilerbau.phases._05_varalloc.AllocationPrettyPrinter.formatVars
import de.thm.mni.compilerbau.table.ParameterType
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.table.VariableEntry

/**
 * This class is used to calculate the memory needed for variables and stack frames of the currently compiled SPL program.
 * Those value have to be stored in their corresponding fields in the [ProcedureEntry], [VariableEntry] and
 * [ParameterType] classes.
 *
 * @param options The options passed to the compiler
 */
class VarAllocator(private val options: CommandLineOptions) {

    fun allocVars(program: Program, table: SymbolTable) {
        if (options.phaseOption == CommandLineOptions.PhaseOption.VARS)
            println(formatVars(program, table));

        TODO() //TODO (assignment 5): Allocate stack slots for all parameters and local variables
    }
}