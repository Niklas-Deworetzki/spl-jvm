package de.thm.mni.compilerbau.types

/**
 * Represents built in types (int, bool) of spl.
 * All available built in types are available as static methods of this class.
 */
class PrimitiveType
/**
 * Creates a new instance for builtin types in SPL.
 *
 * @param byteSize  The size of the type in bytes.
 * @param printName The name of the type. Used for printing it, does not mean the type can be referenced in spl source code with this name.
 */
internal constructor(byteSize: Int, private val printName: String) : Type(byteSize) {
    override fun toString() = printName

    companion object {
        val intType = PrimitiveType(4, "int")
        val boolType = PrimitiveType(4, "boolean")
    }
}