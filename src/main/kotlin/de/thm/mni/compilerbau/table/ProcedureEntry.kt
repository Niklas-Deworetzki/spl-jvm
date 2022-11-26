package de.thm.mni.compilerbau.table

import de.thm.mni.compilerbau.phases._05_varalloc.StackLayout
import java.util.*

/**
 * Represents the table entry for procedure declarations in SPL.
 *
 * @param localTable     The local table containing all local scope specific entries (parameters, local variables).
 * @param parameterTypes A list describing the parameters of the procedure.
 *                       See [ParameterType] for more information.
 */
class ProcedureEntry(val localTable: SymbolTable?, val parameterTypes: List<ParameterType>) : Entry {
    val stackLayout: StackLayout = StackLayout()

    override fun toString() = "proc: (${
        parameterTypes.joinToString(", ", transform = Objects::toString)
    })"

    fun javaMethodDescriptor(): String =
        parameterTypes.joinToString(prefix = "(", postfix = ")V",
        transform = ParameterType::javaTypeDescriptor)

    companion object {
        /**
         * This static method is reserved for the creation of entries for predefined procedures, where the calculations of
         * phase 5 have to be performed manually.
         *
         * @param parameterTypes   A list describing the parameters of the procedure.
         * @param argumentAreaSize The size in byte needed on the stack frame to store all arguments of the procedure.
         * @return A ProcedureEntry containing all necessary information from phase 5 manually computed.
         */
        fun predefinedProcedureEntry(parameterTypes: List<ParameterType>, argumentAreaSize: Int): ProcedureEntry {
            val procedureEntry = ProcedureEntry(null, parameterTypes)
            procedureEntry.stackLayout.argumentAreaSize = argumentAreaSize
            return procedureEntry
        }
    }
}
