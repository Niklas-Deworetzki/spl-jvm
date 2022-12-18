package de.thm.mni.compilerbau.phases._06_codegen

import de.thm.mni.compilerbau.jvm.JavaTypeDescriptors.javaTypeDescriptor
import de.thm.mni.compilerbau.phases._06_codegen.OptimizingIntegerPush.push
import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.Type
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

class VariableInitializer(private val methodWriter: MethodVisitor) {

    fun initialize(variable: VariableEntry) {
        if (variable.type is ArrayType) {
            val dimensions = arrayDimensions(variable.type)
            if (dimensions.size == 1) {
                methodWriter.push(dimensions.first())
                methodWriter.visitIntInsn(NEWARRAY, INTEGER)
            } else {
                for (dimension in dimensions) {
                    methodWriter.push(dimension)
                }
                methodWriter.visitMultiANewArrayInsn(variable.type.javaTypeDescriptor(), dimensions.size)
            }
        } else {
            methodWriter.push(0)
            methodWriter.visitVarInsn(ISTORE, variable.offset)
        }
    }

    private fun arrayDimensions(type: Type): List<Int> {
        val result = mutableListOf<Int>()
        var current = type
        while (current is ArrayType) {
            result.add(current.arraySize)
            current = current.baseType
        }
        return result
    }
}
