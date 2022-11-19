package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor
import de.thm.mni.compilerbau.table.Identifier

/**
 * This class represents the call of another procedure in SPL.
 *
 * Whenever a procedure is called, the name of the procedure has to be provided and additionally a list of expressions
 * whose types match the types of the procedures parameters.
 * All arguments of the call are evaluated and passed to the called procedure which is then executed.
 * The execution of the current procedure is halted until the called procedure returns.
 *
 * @param position      The position of the call in the source code.
 * @param procedureName The identifier of the called procedure.
 * @param arguments     The list of expressions, whose values will be passed to the procedure.
 */
class CallStatement(position: Position, val procedureName: Identifier, val arguments: List<Expression>) : Statement(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString() = formatAst("CallStatement", procedureName, formatAst("Arguments", *arguments.toTypedArray()))
}
