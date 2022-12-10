package de.thm.mni.compilerbau.phases

import de.thm.mni.compilerbau.position.Range

abstract class Pass {
    val errors: MutableList<ErrorReport> = mutableListOf()


    fun reportError(range: Range?, message: String, vararg formatArgs: Any?) {
        errors.add(ErrorReport(range, message.format(formatArgs)))
    }

    fun reportError(message: String, vararg formatArgs: Any?) {
        reportError(null, message, formatArgs)
    }
}