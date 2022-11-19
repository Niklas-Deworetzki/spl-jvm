package de.thm.mni.compilerbau.phases._05_varalloc

/**
 * This class describes the stack frame layout of a procedure.
 * It contains the sizes of the various subareas and provides methods to retrieve information about the stack frame required to generate code for the procedure.
 */
class StackLayout {
    // The following values have to be set in phase 5
    var argumentAreaSize: Int? = null
    var localVarAreaSize: Int? = null
    var outgoingAreaSize: Int? = null
    var isOptimizedLeafProcedure = false // Only relevant for --leafProc

    /**
     * @return The total size of the stack frame described by this object.
     */
    fun frameSize(): Int = TODO()

    /**
     * @return The offset (starting from the new stack pointer) where the old frame pointer is stored in this stack frame.
     */
    fun oldFramePointerOffset(): Int = TODO()

    /**
     * @return The offset (starting from the new frame pointer) where the old return address is stored in this stack frame.
     */
    fun oldReturnAddressOffset(): Int = TODO()
}
