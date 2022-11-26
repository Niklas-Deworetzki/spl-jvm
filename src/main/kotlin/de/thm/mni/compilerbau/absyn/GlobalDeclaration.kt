package de.thm.mni.compilerbau.absyn

import de.thm.mni.compilerbau.table.Identifier

/**
 * This class is the abstract superclass of every global declaration in SPL.
 *
 * Global declarations are all declarations done in the global scope.
 * This declarations may either be a [TypeDeclaration] or a [ProcedureDeclaration].
 *
 * @param name     The identifier for this global declaration.
 */
sealed class GlobalDeclaration(val name: Identifier) : Node()
