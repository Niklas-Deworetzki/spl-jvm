package de.thm.mni.compilerbau.types

/**
 * Represents built in types (int, bool) of spl.
 * All available built in types are available as static methods of this class.
 */
enum class PrimitiveType(private val representation: String) : Type {
    Int("int"),
    Bool("boolean"),
    Bottom("⊥");

    override fun toString(): String = representation
}