package de.thm.mni.compilerbau.table

import de.thm.mni.compilerbau.phases._05_varalloc.StackLayout
import java.util.*

/**
 * Represents the table entry for procedure declarations in SPL.
 *
 * @param localTable     The local table containing all local scope specific entries (parameters, local variables).
 * @param parameterTypes A list describing the parameters of the procedure.
 *                       See [ParameterType] for more information.
 * @param isInternal     This flag is `true` if this procedure is one of SPL's internal procedures provided
 *                       by the library.
 */
class ProcedureEntry(
    val localTable: SymbolTable,
    val parameterTypes: List<ParameterType>,
    val isInternal: Boolean = false
) : Entry {
    lateinit var stackLayout: StackLayout


    override fun toString() = "proc: (${
        parameterTypes.joinToString(", ", transform = Objects::toString)
    })"

    fun javaMethodDescriptor(): String =
        parameterTypes.joinToString(
            prefix = "(", postfix = ")V",
            transform = ParameterType::javaTypeDescriptor
        )

    companion object {
        /**
         * This static method is reserved for the creation of entries for predefined procedures.
         *
         * @param parameterTypes   A list describing the parameters of the procedure.
         */
        fun predefinedProcedureEntry(vararg parameterTypes: ParameterType): ProcedureEntry {
            return ProcedureEntry(SymbolTable(), parameterTypes.toList(), isInternal = true)
        }
    }
}
