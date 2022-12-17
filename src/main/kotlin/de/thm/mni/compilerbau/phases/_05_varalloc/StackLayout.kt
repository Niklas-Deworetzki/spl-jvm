package de.thm.mni.compilerbau.phases._05_varalloc

import de.thm.mni.compilerbau.jvm.InternalType

/**
 * This class describes the stack frame layout of a procedure.
 * It contains the sizes of the various subareas and provides methods to retrieve information about the stack frame required to generate code for the procedure.
 */
class StackLayout(
    val arguments: List<InternalType>,
    val localVariables: List<InternalType>,
    val referencePoolSize: Int
) {

    /**
     * @return The total size of the stack frame described by this object.
     */
    fun frameSize(): Int = TODO()

    fun poolIndexToOffset(referencePoolIndex: Int): Int = TODO()

}
