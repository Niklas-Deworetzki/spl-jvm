package de.thm.mni.compilerbau.jvm

import de.thm.mni.compilerbau.table.ParameterType
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.PrimitiveType
import de.thm.mni.compilerbau.types.Type

/**
 * Utilities to convert internal data structures into Java type descriptors.
 */
object JavaTypeDescriptors {

    fun ProcedureEntry.javaMethodDescriptor(): String =
        parameterTypes.joinToString(
            prefix = "(", postfix = ")V", separator = "",
            transform = ::javaTypeDescriptor
        )

    private fun javaTypeDescriptor(parameterType: ParameterType): String =
        if (parameterType.isReference && parameterType.type == PrimitiveType.Int)
            SplJvmDefinitions.REFERENCE_INTEGER_CLASS_DESCRIPTOR
        else parameterType.type.javaTypeDescriptor()


    fun Type.javaTypeDescriptor(): String = when (this) {
        is ArrayType -> "[" + baseType.javaTypeDescriptor()
        PrimitiveType.Int -> "I"
        PrimitiveType.Bool -> "Z"
        PrimitiveType.Bottom -> "V"
    }
}