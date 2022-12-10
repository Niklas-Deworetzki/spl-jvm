package de.thm.mni.compilerbau.phases._04a_tablebuild

import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.phases.Pass
import de.thm.mni.compilerbau.reporting.Message.Companion.quoted
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.table.TypeEntry
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.PrimitiveType
import de.thm.mni.compilerbau.types.Type

class TypeComputation(private val pass: Pass, val scope: SymbolTable) {

    fun typeOf(typeExpression: TypeExpression): Type = when (typeExpression) {
        is ArrayTypeExpression ->
            ArrayType(typeOf(typeExpression.baseType), typeExpression.arraySize)

        is NamedTypeExpression ->
            when (val entry = scope.lookup(typeExpression.name)) {
                is TypeEntry ->
                    entry.type

                else -> {
                    pass.reportError(
                        typeExpression.position,
                        "Unknown type %s.",
                        typeExpression.name.quoted()
                    )
                    PrimitiveType.Bottom
                }
            }
    }

    fun typeOf(parameterDeclaration: ParameterDeclaration): Type =
        typeOf(parameterDeclaration.typeExpression)

    fun typeOf(variableDeclaration: VariableDeclaration): Type =
        typeOf(variableDeclaration.typeExpression)
}