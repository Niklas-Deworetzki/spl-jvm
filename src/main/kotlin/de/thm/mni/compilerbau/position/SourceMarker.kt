package de.thm.mni.compilerbau.position

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.math.min


class SourceMarker(range: Range) {
    val file: File = range.file

    val line: String
    val lineNumber: Int
    val lineOffset: Int
    val length: Int = (range.offsetEnd - range.offsetStart).toInt()

    init {
        LineReader(InputStreamReader(FileInputStream(file))).use { reader ->
            var remainingChars = range.offsetStart
            var lineNumber = 0
            var line = ""

            do {
                remainingChars -= line.length
                lineNumber++
                line = reader.readLine()
            } while (remainingChars > line.length)

            this.line = line.dropLastWhile { Character.isWhitespace(it) }
            this.lineOffset = remainingChars.toInt().coerceAtMost(this.line.length)
            this.lineNumber = lineNumber
        }
    }

    fun exceedsLine(): Boolean {
        return lineOffset + length > line.length
    }

    fun lineContentsPreceding(): String =
        line.substring(0, lineOffset)

    fun lineContentsSucceeding(): String =
        if (exceedsLine()) "" else line.substring(lineOffset + length)

    fun markedLineContents(): String =
        line.substring(lineOffset, min(lineOffset + length, line.length))
}