package de.thm.mni.compilerbau.table

import de.thm.mni.compilerbau.utils.SplError
import java.util.*

/**
 * Represents a symbol table for a definition scope in SPL.
 * Maps identifiers to the corresponding symbols.
 *
 * @param upperLevel The symbol table for the surrounding scope.
 */
class SymbolTable(private val upperLevel: SymbolTable? = null) {
    private val entries: MutableMap<Identifier, Entry> = HashMap<Identifier, Entry>()

    /**
     * Returns the [SymbolTable] of the upper scope if present.
     *
     * @return The upper [SymbolTable] or empty.
     */
    fun getUpperLevel(): Optional<SymbolTable> {
        return Optional.ofNullable(upperLevel)
    }

    /**
     * Inserts a new symbol into the table.
     * Does nothing if a symbol with this name already exists in this scope.
     *
     * @param name  The name of the symbol that is entered.
     * @param entry The entry for the new symbol.
     */
    fun enter(name: Identifier, entry: Entry) {
        entries.putIfAbsent(name, entry)
    }

    /**
     * Inserts a new symbol into the table.
     * Throws an exception if a symbol with this name already exists in this scope.
     *
     * @param name  The name of the symbol that is entered.
     * @param entry The entry for the new symbol.
     * @param error The exception to throw if a symbol with this name is already defined.
     * @throws SplError If a symbol with this name is already defined.
     */
    fun enter(name: Identifier, entry: Entry, error: SplError) {
        if (entries.containsKey(name)) throw error
        this.enter(name, entry)
    }

    /**
     * Looks for the symbol defined with the given name.
     * Recursively looks in outer scopes if the name is not defined in this scope.
     *
     * @param name The name of the symbol.
     * @return null if no symbol was found, the found symbol otherwise.
     */
    fun lookup(name: Identifier): Entry? = entries[name] ?: upperLevel?.lookup(name)

    /**
     * Looks for the symbol defined with the given name.
     * Recursively looks in outer scopes if the name is not defined in this scope.
     *
     * @param name The name of the symbol.
     * @return The symbol belonging to this name.
     * @throws SplError If there is no symbol with this name.
     * @see SymbolTable.find
     */
    fun lookup(name: Identifier, error: SplError): Entry = find(name).orElseThrow { error }

    /**
     * Tries to find the symbol defined with the given name.
     * If there is no symbol with that name in the current or any outer scope, [Optional.empty] is returned.
     *
     * @param name The name of the symbol.
     * @return The symbol belonging to this name or empty.
     * @see SymbolTable.lookup
     */
    fun find(name: Identifier): Optional<Entry> = Optional.ofNullable(lookup(name))

    /**
     * Converts the table to a human-readable format.
     *
     * @param level       The level of this scope. 0 for the most inner scope, +1 for each outer scope.
     * @return A human readable representation of the table contents.
     */
    fun toString(level: Int): String {
        var string = "  level $level\n"
        if (entries.size == 0) string += "    <empty>\n"
        else {
            string += entries.toSortedMap(compareBy { it.toString() })
                    .map { (key, value): Map.Entry<Identifier, Entry> -> java.lang.String.format("    %-15s --> %s\n", key, value) }
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
