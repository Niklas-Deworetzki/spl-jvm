package de.thm.mni.compilerbau.phases._02_03_parser

import de.thm.mni.compilerbau.position.Range
import de.thm.mni.compilerbau.reporting.Message
import de.thm.mni.compilerbau.reporting.Message.Companion.join
import org.fusesource.jansi.Ansi

class SyntaxErrorReport(private val where: Range, private val expected: List<String>) : Message {

    override fun type(): String =
        "Syntax Error"

    override fun message(): String =
        "Expected " + expected.join()

    override fun color(): Ansi.Color =
        Ansi.Color.RED

    override fun range(): Range =
        where

}
