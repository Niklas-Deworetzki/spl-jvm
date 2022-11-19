package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor

/**
 * Represents a statement without any effect (';').
 *
 * @param position The position of the statement in the source code.
 */
class EmptyStatement(position: Position) : Statement(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("EmptyStatement")
}
