package de.thm.mni.compilerbau.absyn

class Argument(val value: Expression) : Node() {
    lateinit var promotion: PromotionStrategy

    override fun toString(): String = formatAst("Argument", value)

    sealed interface PromotionStrategy
    object NoPromotion : PromotionStrategy
    class Promote(val poolIndex: Int) : PromotionStrategy
}