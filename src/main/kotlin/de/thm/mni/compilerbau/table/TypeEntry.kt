package de.thm.mni.compilerbau.table

import de.thm.mni.compilerbau.types.Type

/**
 * Represents the table entry for type-declarations in SPL.
 *
 * @param type The "meaning" of the type declaration.
 *             Determined by the type expression on the right of the type declaration.
 *             See [Type] and its subclasses.
 */
class TypeEntry(val type: Type) : Entry {
    override fun toString() = "type: $type"
}
