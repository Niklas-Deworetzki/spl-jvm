package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.table.Identifier

/**
 * This class represents the declaration of a type in SPL.
 *
 * When declaring a type, you have to provide a name, which is used as the identifier of this declaration.
 * Additionally a [TypeExpression] has to be provided, which stands on the right hand side of the declaration.
 *
 * @param name           The declarations identifier.
 * @param typeExpression The type expression associated with this declaration.
 */
class TypeDeclaration(name: Identifier, val typeExpression: TypeExpression) : GlobalDeclaration(name) {
    override fun toString(): String = formatAst("TypeDeclaration", name, typeExpression)
}
