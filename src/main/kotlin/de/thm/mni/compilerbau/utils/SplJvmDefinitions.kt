package de.thm.mni.compilerbau.utils

import org.objectweb.asm.Type
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object SplJvmDefinitions {

    fun <T : Any> KClass<T>.javaTypeDescriptor(): String =
        Type.getInternalName(this.java)

    val REFERENCE_INTEGER_CLASS: String = AtomicInteger::class.javaTypeDescriptor()
}