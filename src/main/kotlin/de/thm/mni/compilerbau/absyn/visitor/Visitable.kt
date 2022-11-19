package de.thm.mni.compilerbau.absyn.visitor

/**
 * This class is required to implement the visitor pattern.
 */
interface Visitable {
    fun accept(visitor: Visitor)
}