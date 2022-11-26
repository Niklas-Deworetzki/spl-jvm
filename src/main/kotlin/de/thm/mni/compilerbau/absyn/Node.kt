package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.position.Range
import de.thm.mni.compilerbau.utils.StringOps.indented

/**
 * This abstract class is the root in the hierarchy of AST classes.
 *
 * Every part of the AST has to extend this class.
 */
sealed class Node(@JvmField var position: Range? = null) {

    companion object {

        private fun formatAst(name: String, arguments: List<String>): String {
            if (arguments.isEmpty()) return "$name()"

            val contents = arguments.joinToString(",\n").indented(2)
            return "$name(\n$contents)"
        }

        fun formatAst(name: String, vararg arguments: Any) = formatAst(name, arguments.map(Any::toString).toList())
    }
}
