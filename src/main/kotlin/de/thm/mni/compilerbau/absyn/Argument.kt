package de.thm.mni.compilerbau.absyn

class Argument(val value: Expression) : Node() {
    lateinit var passingMode: PassingMode

    override fun toString(): String = formatAst("Argument", value)

    sealed interface PassingMode
    object ByIntegerValue : PassingMode
    object ByReferenceInteger : PassingMode
    object ByReferenceArray : PassingMode
    class PromoteToReference(val referencePoolIndex: Int) : PassingMode
}
