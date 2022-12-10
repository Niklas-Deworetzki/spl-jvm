package de.thm.mni.compilerbau.reporting

import org.fusesource.jansi.Ansi

/**
 * A builder class used to create textual representations of a [Message].
 *
 *
 * Similar to [StringBuilder] but with optional support of markup.
 */
internal interface MessageBuilder {
    /**
     * Highlights the given text with a bold typeface, if possible.
     */
    fun bold(string: String)

    /**
     * Highlights the given text with a color, if possible.
     */
    fun highlight(string: String)

    /**
     * Simply adds the given text to the builder.
     */
    fun text(string: String)

    /**
     * Returns the [String] created by adding different text blocks
     * (possibly with markup).
     */
    fun finish(): String

    /**
     * A simple [MessageBuilder] implementation without support for markup.
     */
    class TextBuilder : MessageBuilder {
        private val buffer = StringBuilder()
        override fun bold(string: String) {
            text(string)
        }

        override fun highlight(string: String) {
            text(string)
        }

        override fun text(string: String) {
            buffer.append(string)
        }

        override fun finish(): String {
            return buffer.toString()
        }
    }

    /**
     * A [MessageBuilder] implementation that uses ansi codes for markup.
     */
    class AnsiBuilder(private val highlightColor: Ansi.Color) : MessageBuilder {
        private val buffer = Ansi()
        override fun bold(string: String) {
            buffer.bold().a(string).boldOff()
        }

        override fun highlight(string: String) {
            buffer.fg(highlightColor).a(string).fgDefault()
        }

        override fun text(string: String) {
            buffer.a(string)
        }

        override fun finish(): String {
            return buffer.toString()
        }
    }
}
