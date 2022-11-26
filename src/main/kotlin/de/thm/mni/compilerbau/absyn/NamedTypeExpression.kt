package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.table.Identifier

/**
 * This class represents a type expression consisting only of an [Identifier].
 * Example: int
 *
 * In this example, "int" is the [Identifier] of this [NamedTypeExpression].
 *
 * @param name     The identifier used to express the type.
 */
class NamedTypeExpression(val name: Identifier) : TypeExpression() {
    override fun toString(): String = formatAst("NamedTypeExpression", name)
}
