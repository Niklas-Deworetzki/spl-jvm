package de.thm.mni.compilerbau.absyn

/**
 * This class represents a statement that combines a list of statements into a single one.
 *
 * [CompoundStatement]s are used whenever it is necessary to semantically combine multiple statements into a single one.
 * This is for example the case with [WhileStatement]s, which can only hold a single statement as their body.
 *
 * @param statements The list of statements that this statement combines.
 */
class CompoundStatement(val statements: List<Statement>) : Statement() {
    override fun toString(): String = formatAst("CompoundStatement", *statements.toTypedArray())
}
