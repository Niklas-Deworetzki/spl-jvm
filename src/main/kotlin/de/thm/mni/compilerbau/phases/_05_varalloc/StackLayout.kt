package de.thm.mni.compilerbau.phases._05_varalloc

/**
 * This class describes the stack frame layout of a procedure.
 * It contains the sizes of the various subareas and provides methods to retrieve information about the stack frame required to generate code for the procedure.
 */
class StackLayout(
    val argumentAreaSize: Int,
    val localVariableAreaSize: Int,
    val referencesPoolSize: Int
) {

    /**
     * @return The total size of the stack frame described by this object.
     */
    fun frameSize(): Int = TODO()

}
