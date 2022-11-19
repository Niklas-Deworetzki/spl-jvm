package de.thm.mni.compilerbau.utils

object StringOps {
    fun javaLines(str: String): List<String> {
        val lines = str.lines()
        return if (str.endsWith("\n"))
            lines.subList(0, lines.size - 1)
        else
            lines
    }

    fun indent(str: String, indentation: Int): String {
        val indentationPrefix = " ".repeat(indentation)
        return javaLines(str).joinToString("\n") { s -> indentationPrefix + s }
    }

    fun toString(o: Any?): String {
        return o?.toString() ?: "NULL"
    }
}
