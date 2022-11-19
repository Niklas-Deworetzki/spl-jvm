package de.thm.mni.compilerbau.phases._04b_semant

import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.PrimitiveType
import de.thm.mni.compilerbau.types.Type
import de.thm.mni.compilerbau.utils.SplError

/**
 * This class is used to check if the currently compiled SPL program is semantically valid.
 * The body of each procedure has to be checked, consisting of [Statement]s, [Variable]s and [Expression]s.
 * Each node has to be checked for type issues or other semantic issues.
 * Calculated [Type]s can be stored in and read from the dataType field of the [Expression] and [Variable] classes.
 */
object ProcedureBodyChecker {
    fun checkProcedures(program: Program, globalTable: SymbolTable) {

        //TODO (assignment 4b): Check all procedure bodies for semantic errors

        TODO()
    }
}
