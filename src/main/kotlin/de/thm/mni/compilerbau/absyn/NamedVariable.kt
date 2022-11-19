package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor
import de.thm.mni.compilerbau.table.Identifier

/**
 * This class represents a named variable in SPL.
 *
 * Example: `i := 4`
 * In this statement, 'i' is used as a [NamedVariable].
 *
 * A named variable is identified by its name, which is an [Identifier].
 *
 * @param position The position of the variable in the source code.
 * @param name     The identifier of the variable.
 */
class NamedVariable(position: Position, val name: Identifier) : Variable(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("NamedVariable", name)
}
