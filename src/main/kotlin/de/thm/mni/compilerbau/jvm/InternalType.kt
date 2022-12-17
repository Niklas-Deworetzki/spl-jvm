package de.thm.mni.compilerbau.jvm

import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.types.ArrayType

enum class InternalType {
    ARRAY, INTEGER, REFERENCE;

    companion object {
        fun of(variableEntry: VariableEntry): InternalType = when {
            variableEntry.type is ArrayType -> ARRAY
            variableEntry.isReference -> REFERENCE
            else -> INTEGER
        }
    }
}