package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.table.Identifier

/**
 * This class represents the declaration of a procedure in SPL.
 *
 * When declaring a procedure, you have to provide a name, which is used as an identifier in this declaration.
 * Additionally a declaration of a procedure, declares its parameters as a list, a list of local variables and
 * a list of statements in the body of the procedure.
 *
 * @param name       The procedures identifier.
 * @param parameters The procedures parameter list.
 * @param variables  The procedures local variables.
 * @param body       The statements in the procedures body.
 */
class ProcedureDeclaration(
    name: Identifier,
    val parameters: List<ParameterDeclaration>,
    val variables: List<VariableDeclaration>,
    val body: List<Statement>
) : GlobalDeclaration(name) {
    override fun toString(): String = formatAst(
        "ProcedureDeclaration",
        name,
        formatAst("Parameters", *parameters.toTypedArray()),
        formatAst("Variables", *variables.toTypedArray()),
        formatAst("Body", *body.toTypedArray())
    )
}
