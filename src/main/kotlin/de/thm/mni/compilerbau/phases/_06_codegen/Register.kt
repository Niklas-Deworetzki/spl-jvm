package de.thm.mni.compilerbau.phases._06_codegen

class Register(private val number: Int) {
    /**
     * Checks if the register is available for free use, so a value can be stored in it.
     * Only a few of the registers in the ECO32 system, are available for free use. Other registers hold special values
     * like the stack or frame pointer registers or are reserved for the systems use only.
     *
     * @return true is available for free use.
     */
    val isFreeUse: Boolean
        get() = number in 8..23

    /**
     * Returns the register with the number of this
     *
     * @param subtrahend The number to subtract from this register's number.
     * @return the new register
     */
    operator fun minus(subtrahend: Int) = Register(number - subtrahend)

    /**
     * @return The register preceding this register.
     */
    fun previous() = Register(number - 1)

    /**
     * @return The register following this register.
     */
    operator fun next() = Register(number + 1)

    override fun toString() = "$$number"
}
