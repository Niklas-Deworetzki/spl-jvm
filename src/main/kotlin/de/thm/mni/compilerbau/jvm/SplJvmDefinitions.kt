package de.thm.mni.compilerbau.jvm

import org.objectweb.asm.Type
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object SplJvmDefinitions {

    fun <T : Any> KClass<T>.javaInternalName(): String =
        Type.getInternalName(this.java)

    fun <T : Any> KClass<T>.javaTypeDescriptor(): String =
        Type.getDescriptor(this.java)

    val REFERENCE_INTEGER_CLASS: String = AtomicInteger::class.javaTypeDescriptor()

    val REFERENCE_INTEGER_METHOD_GET = object : LibraryMethod {
        override val name = "get"
        override val descriptor = "()I"
    }

    val REFERENCE_INTEGER_METHOD_SET = object : LibraryMethod {
        override val name = "set"
        override val descriptor = "(I)V"
    }

    interface LibraryMethod {
        val name: String
        val descriptor: String
    }
}