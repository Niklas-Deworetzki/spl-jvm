package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.types.Type

/**
 * This class is the abstract superclass of all type expressions in SPL.
 *
 * A type expression is either a [NamedTypeExpression] or an [ArrayTypeExpression].
 * They behave like a formula representing a concrete semantic [Type] which has to be calculated
 * during phase 4.
 */
sealed class TypeExpression(position: Position) : Node(position) {
    var dataType: Type? = null
}
