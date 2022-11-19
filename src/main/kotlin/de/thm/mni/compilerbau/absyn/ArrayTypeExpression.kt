package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.absyn.visitor.Visitor

/**
 * This class represents a type expression, for the type of a fixed-size array of another type.
 * Example: array [ 10 ] of int
 *
 * In this example, the base type of this expression, is the [NamedTypeExpression] with "int" as identifier.
 * The size of this array is defined by the literal 10.
 *
 * @param position  The position of the type expression in the source code.
 * @param arraySize The number of elements an array of this type can hold.
 * @param baseType  The type expression of the elements type.
 */
class ArrayTypeExpression(position: Position, val arraySize: Int, val baseType: TypeExpression) : TypeExpression(position) {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun toString(): String = formatAst("ArrayTypeExpression", baseType, arraySize)
}
