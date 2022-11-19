package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor

/**
 * This class represents an array access in SPL.
 *
 * Example: vector[3]
 * In this example, vector is the accessed array and the literal 3 is the index of the access.
 *
 * @param position The position of the array access in the source code.
 * @param array    The variable representing the accessed array.
 * @param index    The expression representing the index of the access.
 */
class ArrayAccess(position: Position, val array: Variable, val index: Expression) : Variable(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("ArrayAccess", array, index)
}
