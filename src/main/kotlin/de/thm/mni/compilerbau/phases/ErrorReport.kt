package de.thm.mni.compilerbau.phases

import de.thm.mni.compilerbau.position.Range
import de.thm.mni.compilerbau.reporting.Message
import org.fusesource.jansi.Ansi

data class ErrorReport(val range: Range?, val message: String) : Message {

    override fun type(): String =
        "Error"

    override fun message(): String =
        message

    override fun color(): Ansi.Color =
        Ansi.Color.RED

    override fun range(): Range? =
        range
}
