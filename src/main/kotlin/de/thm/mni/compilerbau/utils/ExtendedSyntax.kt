package de.thm.mni.compilerbau.utils

import de.thm.mni.compilerbau.absyn.*

object ExtendedSyntax {

    fun ProcedureDeclaration.forEachStatement(action: (Statement) -> Unit) {
        for (statement in this.body) {
            statement.forEachChild(action)
        }
    }

    private fun Statement.forEachChild(action: (Statement) -> Unit): Unit = when (this) {
        is CompoundStatement ->
            for (statement in this.statements) {
                statement.forEachChild(action)
            }

        is IfStatement -> {
            this.thenPart.forEachChild(action)
            this.elsePart.forEachChild(action)
        }

        is WhileStatement ->
            this.body.forEachChild(action)

        else ->
            action(this)
    }

    fun Argument.asVariable(): Variable =
        (this.value as VariableExpression).variable

}