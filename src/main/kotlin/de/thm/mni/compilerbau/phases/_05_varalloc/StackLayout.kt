package de.thm.mni.compilerbau.phases._05_varalloc

/**
 * This class describes the stack frame layout of a procedure.
 * It contains the sizes of the various subareas and provides methods to retrieve information about the stack frame required to generate code for the procedure.
 */
class StackLayout(
    argumentAreaSize: Int,
    localVariablesAreaSize: Int,
    val referencePoolSize: Int
) {
    private val poolOffset: Int = argumentAreaSize + localVariablesAreaSize

    fun poolIndexToOffset(referencePoolIndex: Int): Int = poolOffset + referencePoolIndex

    fun referencePoolOffsets(): IntRange = poolOffset until poolOffset + referencePoolSize
}
