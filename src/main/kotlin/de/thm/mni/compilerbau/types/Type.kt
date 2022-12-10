package de.thm.mni.compilerbau.types

/**
 * Represents the semantic type of expressions and variables.
 * Not to be confused with [de.thm.mni.compilerbau.absyn.TypeExpression].
 */
sealed interface Type {

    fun javaTypeDescriptor(): String = when (this) {
        is ArrayType -> "[" + this.baseType.javaTypeDescriptor()
        PrimitiveType.Int -> "I"
        PrimitiveType.Bool -> "Z"
        PrimitiveType.Bottom -> "V"
    }
}
