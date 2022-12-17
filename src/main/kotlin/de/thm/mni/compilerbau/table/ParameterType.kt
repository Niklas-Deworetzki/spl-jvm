package de.thm.mni.compilerbau.table

import de.thm.mni.compilerbau.types.Type

/**
 * Contains the information about a parameter, that are necessary when calling the associated procedure.
 *
 * @param type        The semantic type of the parameter. See [Type] and its subclasses.
 * @param isReference If the parameter is a reference parameter.
 */
class ParameterType(val type: Type, val isReference: Boolean) {
    override fun toString() = "${if (isReference) "ref " else ""}$type"
}
