package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor
import de.thm.mni.compilerbau.table.Identifier

/**
 * This class represents the local declaration of a variable.
 *
 * Variables are declared inside a procedure and combine an [Identifier] with a [TypeExpression],
 * expressing the variables type.
 * Variables are only visible in the local scope of their procedure.
 *
 * @param position       The position of the declaration in the source code.
 * @param name           The identifier of the declared local variable.
 * @param typeExpression The type expression used to express the type of the local variable.
 */
class VariableDeclaration(position: Position, val name: Identifier, val typeExpression: TypeExpression) : Node(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("VariableDeclaration", name, typeExpression)
}
