package de.thm.mni.compilerbau.reporting

import de.thm.mni.compilerbau.position.Range
import org.fusesource.jansi.Ansi

/**
 * Common interface for all message variants.
 */
interface Message {

    /**
     * Provides the message type as a [String] used as a
     * header for a formatted message. This should be capitalized.
     */
    fun type(): String

    /**
     * Contents of the message.
     */
    fun message(): String

    /**
     * A description of the source code associated with the message.
     */
    fun range(): Range? {
        return null
    }

    /**
     * A highlighting color used when formatting the message.
     */
    fun color(): Ansi.Color


    companion object {
        /**
         * Returns a [String] representation of the given [Object]
         * with added quotes. Used to highlight identifiers and mark them as
         * different from the remaining text.
         */
        fun quote(obj: Any): String {
            return "`$obj'"
        }

        fun Any.quoted(): String =
            quote(this)


        fun <T> List<T>.join(defaultSeparator: String = ",", lastSeparator: String = "or"): String = when (size) {
            0 -> ""
            1 -> first().toString()
            else -> subList(0, size - 1).joinToString(defaultSeparator) + lastSeparator + last().toString()
        }
    }
}