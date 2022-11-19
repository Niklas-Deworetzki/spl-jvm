package de.thm.mni.compilerbau.absyn

/**
 * This class is the abstract superclass of every statement in SPL.
 *
 * There exist many different statements present in SPL, which may all occur in the body of a procedure.
 */
sealed class Statement(position: Position) : Node(position)
