package de.thm.mni.compilerbau.reporting

import de.thm.mni.compilerbau.position.SourceMarker
import org.fusesource.jansi.Ansi
import java.io.IOException


/**
 * Utility class used to create human-readable textual representations of
 * [Message] instances. This class uses [MessageBuilder] instances
 * to create multiple lines of text. The [MessageBuilder] instances used
 * by this class will preferably be [MessageBuilder.AnsiBuilder] instances,
 * if ansi support is provided. Otherwise, plain text messages will be constructed.
 */
object MessageFormatter {

    private fun createBuilder(message: Message): MessageBuilder =
        if (Ansi.isEnabled()) MessageBuilder.AnsiBuilder(message.color())
        else MessageBuilder.TextBuilder()

    private fun messageLines(message: Message): List<String> {
        val linesBuffer: MutableList<String> = ArrayList()

        linesBuffer.add(messageHeader(message))
        for (detail in message.details()) {
            linesBuffer.add(messageDetail(detail))
        }

        val hint = message.hint()
        if (hint != null) {
            linesBuffer.add(messageHint(hint))
        }

        linesBuffer.addAll(messageLocation(message))
        return linesBuffer
    }

    private fun messageHeader(message: Message): String {
        val builder = createBuilder(message)
        builder.highlight(message.type())
        builder.text(DETAILS_SEPARATOR)
        builder.text(message.message())
        return builder.finish()
    }

    private fun messageDetail(detail: Map.Entry<String, Any?>): String {
        return DETAILS_INDENT + detail.key + DETAILS_SEPARATOR + detail.value
    }

    private fun messageHint(hint: String): String {
        return DETAILS_INDENT + HINT_KEY + DETAILS_SEPARATOR + hint
    }

    private fun messageLocation(message: Message): List<String> {
        val range = message.range() ?: return emptyList()

        return try {
            val marker = SourceMarker(range)
            listOf(
                FILE_INDICATOR_FORMAT.format(Message.quote(range.file.path)),
                markerLine(message, marker),
                markerHint(message, marker)
            )
        } catch (unableToReconstructFile: IOException) {
            listOf(
                FILE_INDICATOR_UNKNOWN_POSITION_FORMAT.format(Message.quote(range.file.path))
            )
        }
    }

    private fun markerLine(message: Message, marker: SourceMarker): String {
        val builder = createBuilder(message)
        builder.text(INDICATOR_INDENT)
        builder.bold(marker.lineNumber.toString())
        builder.text(INDICATOR_SEPARATOR)
        builder.text(marker.lineContentsPreceding())
        builder.highlight(marker.markedLineContents())
        builder.text(marker.lineContentsSucceeding())
        return builder.finish()
    }

    private fun markerHint(message: Message, marker: SourceMarker): String {
        val builder = createBuilder(message)
        builder.text(INDICATOR_INDENT)
        val lineNumberDigits: Int = marker.lineNumber.toString().length
        builder.text(MARKER_INDENT_DEFAULT_CHAR.repeat(lineNumberDigits))
        builder.text(INDICATOR_SEPARATOR)
        builder.text(marker.lineContentsPreceding().asWhitespaceString())
        builder.highlight(createHintFragment(marker))
        return builder.finish()
    }

    private const val DETAILS_INDENT = "  "
    private const val DETAILS_SEPARATOR = ": "
    private const val HINT_KEY = "Hint"
    private const val MARKER_INDENT_DEFAULT_CHAR = ' '
    private const val MARKER_TERMINATOR_CHAR = '^'
    private const val MARKER_MIDDLE_CHAR = '~'
    private const val INDICATOR_INDENT = " "
    private const val INDICATOR_SEPARATOR = " | "
    private const val FILE_INDICATOR_FORMAT = "In file %s:"
    private const val FILE_INDICATOR_UNKNOWN_POSITION_FORMAT = "At unknown position in file %s:"

    /**
     * Creates a multiline textual representation of the given [Message].
     * The returned [String] possibly contains ansi codes for markup.
     */
    fun format(message: Message): String =
        messageLines(message).joinToString(System.lineSeparator())

    private fun createHintFragment(marker: SourceMarker): String {
        return if (marker.exceedsLine()) {
            val charsToEndOfLine = marker.line.length - marker.lineOffset
            MARKER_TERMINATOR_CHAR +
                    MARKER_MIDDLE_CHAR.repeat(charsToEndOfLine)
        } else if (marker.length < 2) {
            MARKER_TERMINATOR_CHAR.toString()
        } else {
            val charsWithinMarker = marker.length - 2
            MARKER_TERMINATOR_CHAR +
                    MARKER_MIDDLE_CHAR.repeat(charsWithinMarker) +
                    MARKER_TERMINATOR_CHAR
        }
    }

    private fun String.asWhitespaceString(): String {
        val result = this.toCharArray()
        for (i in result.indices) {
            if (!Character.isWhitespace(result[i])) {
                result[i] = MARKER_INDENT_DEFAULT_CHAR
            }
        }
        return String(result)
    }

    private fun Char.repeat(times: Int): String {
        return this.toString().repeat(times)
    }
}
