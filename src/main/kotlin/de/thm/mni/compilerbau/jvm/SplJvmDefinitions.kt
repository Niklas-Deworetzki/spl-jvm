package de.thm.mni.compilerbau.jvm

import org.objectweb.asm.Type
import kotlin.reflect.KClass

object SplJvmDefinitions {

    const val GENERATED_CLASS_NAME: String = "Spl"

    const val LIBRARY_CLASS_NAME: String = "SplLib"

    fun <T : Any> KClass<T>.javaInternalName(): String =
        Type.getInternalName(this.java)

    const val REFERENCE_INTEGER_CLASS_DESCRIPTOR: String = "LIntRef;"

    const val REFERENCE_INTEGER_CLASS_NAME: String = "IntRef"

    const val REFERENCE_INTEGER_VALUE_NAME: String = "value"

    const val REFERENCE_INTEGER_VALUE_DESCRIPTOR: String = "I"
}