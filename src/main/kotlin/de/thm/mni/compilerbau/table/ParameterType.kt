package de.thm.mni.compilerbau.table

import de.thm.mni.compilerbau.types.PrimitiveType
import de.thm.mni.compilerbau.types.Type
import de.thm.mni.compilerbau.utils.SplJvmDefinitions

/**
 * Contains the information about a parameter, that are necessary when calling the associated procedure.
 *
 * @param type        The semantic type of the parameter. See [Type] and its subclasses.
 * @param isReference If the parameter is a reference parameter.
 */
class ParameterType(val type: Type, val isReference: Boolean) {
    var offset: Int? = null // This value has to be set in phase 5

    /**
     * @param type        The semantic type of the parameter. See [Type] and its subclasses.
     * @param isReference If the parameter is a reference parameter.
     * @param offset      The stack offset of this parameter when calling the associated procedure in respect to the stack pointer
     */
    constructor(type: Type, isReference: Boolean, offset: Int) : this(type, isReference) {
        this.offset = offset
    }

    override fun toString() = "${if (isReference) "ref " else ""}$type"

    fun javaTypeDescriptor(): String =
        if (isReference && type == PrimitiveType.Int) SplJvmDefinitions.REFERENCE_INTEGER_CLASS
        else type.javaTypeDescriptor()
}
