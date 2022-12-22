package de.thm.mni.compilerbau.absyn

/**
 * This class represents values passed as an [Argument] to a [CallStatement](called procedure).
 *
 * @param value The value passed as an argument.
 */
class Argument(val value: Expression) : Node() {
    lateinit var passingMode: PassingMode

    override fun toString(): String = formatAst("Argument", value)

    /**
     * Determines how the argument is passed to the callee.
     */
    sealed interface PassingMode

    /**
     * Argument is passed as an integer by value.
     */
    object ByIntegerValue : PassingMode

    /**
     * Argument is passed as an integer by reference without promotion.
     */
    object ByReferenceInteger : PassingMode

    /**
     * Argument is passed as an array by reference.
     */
    object ByReferenceArray : PassingMode

    /**
     * Argument is passed as an integer by reference using the pool variable at the
     * given index for argument promotion.
     */
    class PromoteToReference(val referencePoolIndex: Int) : PassingMode
}
