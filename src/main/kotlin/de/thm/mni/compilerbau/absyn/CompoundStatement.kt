package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor

/**
 * This class represents a statement that combines a list of statements into a single one.
 *
 * [CompoundStatement]s are used whenever it is necessary to semantically combine multiple statements into a single one.
 * This is for example the case with [WhileStatement]s, which can only hold a single statement as their body.
 *
 * @param position   The position of the statement in the source code.
 * @param statements The list of statements that this statement combines.
 */
class CompoundStatement(position: Position, val statements: List<Statement>) : Statement(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("CompoundStatement", *statements.toTypedArray())
}
