package de.thm.mni.compilerbau.position

import java.io.EOFException
import java.io.IOException
import java.io.Reader


/**
 * [Reader] subclass providing a [LineReader.readLine]
 * method that returns the next line **including** line separators.
 */
internal class LineReader(private val reader: Reader) : Reader() {
    private val lineBuilder = StringBuilder()

    private fun finalizeLine(): String {
        val result = lineBuilder.toString()
        lineBuilder.setLength(0)
        return result
    }

    private var isAtEnd = false

    @Throws(IOException::class)
    fun readLine(): String {
        if (isAtEnd) throw EOFException()
        do {
            val current = reader.read()
            if (current == -1) {
                isAtEnd = true
                return finalizeLine()
            }
            lineBuilder.append(current.toChar())
            if (current == '\n'.code) {
                return finalizeLine()
            } else if (current == '\r'.code) {
                return when (val next = reader.read()) {
                    -1 -> {
                        isAtEnd = true
                        finalizeLine()
                    }
                    '\n'.code -> {
                        lineBuilder.append(next.toChar())
                        finalizeLine()
                    }
                    else -> {
                        val result = finalizeLine()
                        lineBuilder.append(next.toChar())
                        result
                    }
                }
            }
        } while (true)
    }

    @Throws(IOException::class)
    override fun read(cbuf: CharArray, off: Int, len: Int): Int =
        reader.read(cbuf, off, len)

    @Throws(IOException::class)
    override fun close() =
        reader.close()
}
