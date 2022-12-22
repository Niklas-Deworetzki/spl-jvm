package de.thm.mni.compilerbau.phases._06_codegen

import de.thm.mni.compilerbau.jvm.JavaTypeDescriptors.javaTypeDescriptor
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions.REFERENCE_INTEGER_CLASS_NAME
import de.thm.mni.compilerbau.phases._05_varalloc.StackLayout
import de.thm.mni.compilerbau.phases._06_codegen.OptimizingIntegerPush.push
import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.Type
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

/**
 * Class used to initialize local variables according to their types.
 */
class VariableInitializer(private val methodWriter: MethodVisitor) {

    fun initializeReferencePool(layout: StackLayout) {
        for (offset in layout.referencePoolOffsets()) {
            methodWriter.visitTypeInsn(NEW, REFERENCE_INTEGER_CLASS_NAME)
            methodWriter.visitInsn(DUP)
            methodWriter.visitMethodInsn(
                INVOKESPECIAL,
                REFERENCE_INTEGER_CLASS_NAME,
                "<init>",
                "()V",
                false
            )
            methodWriter.visitVarInsn(ASTORE, offset)
        }
    }

    fun initialize(variable: VariableEntry) {
        if (variable.type is ArrayType) {
            val dimensions = arrayDimensions(variable.type)
            if (dimensions.size == 1) {
                methodWriter.push(dimensions.first())
                methodWriter.visitIntInsn(NEWARRAY, T_INT)
            } else {
                for (dimension in dimensions) {
                    methodWriter.push(dimension)
                }
                methodWriter.visitMultiANewArrayInsn(variable.type.javaTypeDescriptor(), dimensions.size)
            }
            methodWriter.visitVarInsn(ASTORE, variable.offset)
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
