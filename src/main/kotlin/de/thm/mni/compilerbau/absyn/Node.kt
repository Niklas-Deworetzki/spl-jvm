package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.position.Range
import de.thm.mni.compilerbau.utils.StringOps.indented

/**
 * This abstract class is the root in the hierarchy of AST classes.
 *
 * Every part of the AST has to extend this class.
 */
sealed class Node {
    lateinit var position: Range

    companion object {

        private fun formatAst(name: String, arguments: List<String>): String {
            if (arguments.isEmpty()) return "$name()"

            val contents = arguments.joinToString(",\n").indented(2)
            return "$name(\n$contents)"
        }

        fun formatAst(name: String, vararg arguments: Any) = formatAst(name, arguments.map(Any::toString).toList())

        fun <N : Node> N.withPosition(range: Range): N {
            this.position = range
            return this
        }
    }
}
