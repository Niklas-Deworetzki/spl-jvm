package de.thm.mni.compilerbau.utils

object StringOps {
    private fun javaLines(str: String): List<String> {
        val lines = str.lines()
        return if (str.endsWith("\n"))
            lines.subList(0, lines.size - 1)
        else
            lines
    }

    fun String.indented(indentation: Int): String {
        val indentationPrefix = " ".repeat(indentation)
        return javaLines(this).joinToString("\n") { s -> indentationPrefix + s }
    }
}
