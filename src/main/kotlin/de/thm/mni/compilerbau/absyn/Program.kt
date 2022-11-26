package de.thm.mni.compilerbau.absyn

/**
 * This class represents the root of the AST.
 *
 * A program in SPL consists of a list of global declarations ([TypeDeclaration] and [ProcedureDeclaration]).
 *
 * @param declarations The list of global declarations in the SPL program.
 */
class Program(val declarations: List<GlobalDeclaration>) : Node() {
    override fun toString(): String = formatAst("Program", *declarations.toTypedArray())
}
