package de.thm.mni.compilerbau.absyn

/**
 * This class represents the position in the source code of any [Node].
 */
data class Position(val line: Int = -1, val column: Int = -1) {
    companion object {
        /**
         * This variable is used as a placeholder when no position is present.
         * For example when throwing an error for a missing main procedure, which is required for a SPL program to have,
         * there is no position for this missing procedure. In such cases, this value is used.
         */
        @JvmField
        val ERROR_POSITION = Position(-1, -1)
    }
}
