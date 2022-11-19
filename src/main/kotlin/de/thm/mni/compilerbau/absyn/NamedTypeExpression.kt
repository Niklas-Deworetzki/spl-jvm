package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor
import de.thm.mni.compilerbau.table.Identifier

/**
 * This class represents a type expression consisting only of an [Identifier].
 * Example: int
 *
 * In this example, "int" is the [Identifier] of this [NamedTypeExpression].
 *
 * @param position The position of the type expression in the source code.
 * @param name     The identifier used to express the type.
 */
class NamedTypeExpression(position: Position, val name: Identifier) : TypeExpression(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("NamedTypeExpression", name)
}
