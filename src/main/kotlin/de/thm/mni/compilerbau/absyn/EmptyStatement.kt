package de.thm.mni.compilerbau.absyn

/**
 * Represents a statement without any effect (';').
 */
class EmptyStatement : Statement() {
    override fun toString(): String = formatAst("EmptyStatement")
}
