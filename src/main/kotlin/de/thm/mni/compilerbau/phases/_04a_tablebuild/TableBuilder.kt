package de.thm.mni.compilerbau.phases._04a_tablebuild

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.table.*
import de.thm.mni.compilerbau.types.Type

/**
 * This class is used to create and populate a [SymbolTable] containing entries for every symbol in the currently
 * compiled SPL program.
 * Every declaration of the SPL program needs its corresponding entry in the [SymbolTable].
 *
 * Calculated [Type]s can be stored in and read from the dataType field of the [Expression],
 * [TypeExpression] or [Variable] classes.
 */

class TableBuilder(private val options: CommandLineOptions) {

    fun buildSymbolTable(program: Program): SymbolTable {
        //TODO (assignment 4a): Initialize a symbol table with all predefined symbols and fill it with user-defined symbols

        TODO()
    }

    companion object {
        /**
         * Prints the local symbol table of a procedure together with a heading-line
         * NOTE: You have to call this after completing the local table to support '--tables'.
         *
         * @param name  The name of the procedure
         * @param entry The entry of the procedure to print
         */
        private fun printSymbolTableAtEndOfProcedure(name: Identifier, entry: ProcedureEntry) {
            println("Symbol table at end of procedure '$name':")
            println(entry.localTable.toString())
        }
    }
}
