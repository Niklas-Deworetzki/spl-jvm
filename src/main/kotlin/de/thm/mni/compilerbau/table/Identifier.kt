package de.thm.mni.compilerbau.table

/**
 * Represents an identifier in SPL.
 * Implements string interning internally to speed up lookups in symbol tables.
 */
class Identifier(identifier: String) {
    // Intern the identifier.
    // This way string equality in table lookups can be determined by only comparing the references.
    private val identifier: String = identifier.intern()

    override fun hashCode(): Int = identifier.hashCode()
    override fun equals(other: Any?): Boolean = other is Identifier && other.identifier == identifier
    override fun toString(): String = identifier
}
