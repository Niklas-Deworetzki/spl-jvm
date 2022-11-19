package de.thm.mni.compilerbau.types

/**
 * Represents fixed-size array types in spl.
 * Is constructed each time an [de.thm.mni.compilerbau.absyn.ArrayTypeExpression] is encountered in the source code.
 *
 * @param baseType  The type of the array's elements.
 * @param arraySize The number of elements in an array of this type.
 */
class ArrayType(val baseType: Type, val arraySize: Int) : Type(arraySize * baseType.byteSize) {
    override fun toString() = "array [$arraySize] of $baseType"
}
