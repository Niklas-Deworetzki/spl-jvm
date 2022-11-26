package de.thm.mni.compilerbau.phases._06_codegen

import de.thm.mni.compilerbau.absyn.Variable

data class ReferenceArgument(val referenceCacheOffset: Int, val argument: Variable)
