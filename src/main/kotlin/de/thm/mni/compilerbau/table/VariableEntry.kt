package de.thm.mni.compilerbau.table

import de.thm.mni.compilerbau.types.Type
import kotlin.properties.Delegates

/**
 * Represents the table entry for variable- and parameter-declarations in SPL.
 * Please note that parameters of a procedure will also need to be entered as VariableEntries, there is no separate ParameterEntry.
 * Parameters also have an associated [ParameterType], that needs to be added to the associated procedure's ProcedureEntry.
 *
 * @param type        The semantic type of the variable. Calculated by looking at the respective type expression.
 * @param isReference If the variable is a reference.
 *                    Only ever true for reference parameters, false for non-reference parameters and local variable.
 */
class VariableEntry(val type: Type, val isReference: Boolean) : Entry {
    var offset by Delegates.notNull<Int>()  // This value has to be set in phase 5

    override fun toString() = "var: ${if (isReference) "ref " else ""}$type"
}
