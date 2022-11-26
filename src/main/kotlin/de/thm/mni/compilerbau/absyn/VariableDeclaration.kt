package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.table.Identifier

/**
 * This class represents the local declaration of a variable.
 *
 * Variables are declared inside a procedure and combine an [Identifier] with a [TypeExpression],
 * expressing the variables type.
 * Variables are only visible in the local scope of their procedure.
 *
 * @param name           The identifier of the declared local variable.
 * @param typeExpression The type expression used to express the type of the local variable.
 */
class VariableDeclaration(val name: Identifier, val typeExpression: TypeExpression) : Node() {
    override fun toString(): String = formatAst("VariableDeclaration", name, typeExpression)
}
