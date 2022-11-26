package de.thm.mni.compilerbau.absyn

/**
 * This class represents an array access in SPL.
 *
 * Example: vector[3]
 * In this example, vector is the accessed array and the literal 3 is the index of the access.
 *
 * @param array    The variable representing the accessed array.
 * @param index    The expression representing the index of the access.
 */
class ArrayAccess(val array: Variable, val index: Expression) : Variable() {
    override fun toString(): String = formatAst("ArrayAccess", array, index)
}
