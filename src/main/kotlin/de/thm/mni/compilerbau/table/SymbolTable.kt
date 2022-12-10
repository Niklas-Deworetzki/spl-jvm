package de.thm.mni.compilerbau.table

import java.util.*

/**
 * Represents a symbol table for a definition scope in SPL.
 * Maps identifiers to the corresponding symbols.
 *
 * @param upperLevel The symbol table for the surrounding scope.
 */
class SymbolTable(val upperLevel: SymbolTable? = null) {
    private val entries: MutableMap<Identifier, Entry> = HashMap<Identifier, Entry>()

    /**
     * Inserts a new symbol into the table.
     * Does nothing if a symbol with this name already exists in this scope.
     *
     * @param name  The name of the symbol that is entered.
     * @param entry The entry for the new symbol.
     */
    fun enter(name: Identifier, entry: Entry): Boolean =
        entries.putIfAbsent(name, entry) == null

    /**
     * Looks for the symbol defined with the given name.
     * Recursively looks in outer scopes if the name is not defined in this scope.
     *
     * @param name The name of the symbol.
     * @return null if no symbol was found, the found symbol otherwise.
     */
    fun lookup(name: Identifier): Entry? = entries[name] ?: upperLevel?.lookup(name)

    /**
     * Converts the table to a human-readable format.
     *
     * @param level       The level of this scope. 0 for the most inner scope, +1 for each outer scope.
     * @return A human readable representation of the table contents.
     */
    fun toString(level: Int): String {
        var string = "  level $level\n"
        if (entries.isEmpty()) string += "    <empty>\n"
        else {
            string += entries.toSortedMap(compareBy { it.toString() })
                .map { (key, value) -> String.format("    %-15s --> %s\n", key, value) }
                .joinToString(separator = "")
        }
        if (upperLevel != null) string += upperLevel.toString(level + 1)
        return string
    }

    /**
     * @return A human readable representation of the table contents.
     */
    override fun toString() = this.toString(0)
}
