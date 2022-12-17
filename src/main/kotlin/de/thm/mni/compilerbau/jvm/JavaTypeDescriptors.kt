package de.thm.mni.compilerbau.jvm

import de.thm.mni.compilerbau.table.ParameterType
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.PrimitiveType
import de.thm.mni.compilerbau.types.Type

object JavaTypeDescriptors {

    fun ProcedureEntry.javaMethodDescriptor(): String =
        parameterTypes.joinToString(
            prefix = "(", postfix = ")V", separator = "",
            transform = ::javaTypeDescriptor
        )

    private fun javaTypeDescriptor(parameterType: ParameterType): String =
        if (parameterType.isReference && parameterType.type == PrimitiveType.Int)
            SplJvmDefinitions.REFERENCE_INTEGER_CLASS
        else javaTypeDescriptor(parameterType.type)


    private fun javaTypeDescriptor(type: Type): String = when (type) {
        is ArrayType -> "[" + javaTypeDescriptor(type.baseType)
        PrimitiveType.Int -> "I"
        PrimitiveType.Bool -> "Z"
        PrimitiveType.Bottom -> "V"
    }
}