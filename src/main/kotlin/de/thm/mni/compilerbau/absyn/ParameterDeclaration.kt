package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.table.Identifier

/**
 * This class represents the local declaration of a parameter in SPL.
 *
 * Parameters are declared in the parameter list of a procedure. They combine an [Identifier] with
 * a [TypeExpression], expressing the parameters type and additionally have to store
 * whether the parameter is passed as a reference.
 * Parameters are only visible in the local scope of their procedure.
 *
 * @param name           The identifier of the declared parameter.
 * @param typeExpression The type expression used to express the parameters type.
 * @param isReference    A boolean value used to represent whether the parameter is passed as a reference.
 */
class ParameterDeclaration(val name: Identifier, val typeExpression: TypeExpression, val isReference: Boolean) :
    Node() {
    override fun toString(): String = formatAst("ParameterDeclaration", name, typeExpression, isReference)
}
