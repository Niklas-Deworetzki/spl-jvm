package de.thm.mni.compilerbau.types

/**
 * Represents the semantic type of expressions and variables.
 * Not to be confused with [de.thm.mni.compilerbau.absyn.TypeExpression].
 *
 * @param byteSize The size in bytes this type uses in memory.
 *                 Example: 4 for 'int', 12 for 'array[3] of int'
 */
sealed class Type constructor(    val byteSize: Int)
