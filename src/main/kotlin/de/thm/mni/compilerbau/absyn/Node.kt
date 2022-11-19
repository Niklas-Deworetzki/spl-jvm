package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitable
import de.thm.mni.compilerbau.utils.StringOps

/**
 * This abstract class is the root in the hierarchy of AST classes.
 *
 * Every part of the AST has to extend this class.
 */
sealed class Node(@JvmField val position: Position) : Visitable {

    companion object {

        private fun formatAst(name: String, arguments: List<String>): String {
            val indent = 2
            return if (arguments.isEmpty()) "$name()"
            else "$name(\n${StringOps.indent(arguments.joinToString(",\n"), indent)})"

        }

        fun formatAst(name: String, vararg arguments: Any) = formatAst(name, arguments.map { it.toString() }.toList())
    }
}
