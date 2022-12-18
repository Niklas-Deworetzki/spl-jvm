package de.thm.mni.compilerbau.phases._06_codegen

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

object OptimizingIntegerPush {

    private val BYTE_RANGE = Byte.MIN_VALUE..Byte.MAX_VALUE
    private val SHORT_RANGE = Short.MIN_VALUE..Short.MAX_VALUE

    fun MethodVisitor.push(value: Int): Unit = when (value) {
        0 -> visitInsn(ICONST_0)
        1 -> visitInsn(ICONST_1)
        2 -> visitInsn(ICONST_2)
        3 -> visitInsn(ICONST_3)
        4 -> visitInsn(ICONST_4)
        5 -> visitInsn(ICONST_5)
        -1 -> visitInsn(ICONST_M1)
        in BYTE_RANGE -> visitIntInsn(BIPUSH, value)
        in SHORT_RANGE -> visitIntInsn(SIPUSH, value)
        else -> visitLdcInsn(value)
    }
}