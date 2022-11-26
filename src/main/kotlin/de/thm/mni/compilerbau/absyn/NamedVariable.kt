package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.table.Identifier

/**
 * This class represents a named variable in SPL.
 *
 * Example: `i := 4`
 * In this statement, 'i' is used as a [NamedVariable].
 *
 * A named variable is identified by its name, which is an [Identifier].
 *
 * @param name     The identifier of the variable.
 */
class NamedVariable(val name: Identifier) : Variable() {
    override fun toString(): String = formatAst("NamedVariable", name)
}
