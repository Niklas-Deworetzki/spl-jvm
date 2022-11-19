package de.thm.mni.compilerbau.utils

class AsciiGraphicalTableBuilder {
    enum class Alignment {
        LEFT, CENTER;

        fun pad(s: String, width: Int, paddingChar: Char): String {
            val padding = paddingChar.toString()
            return when (this) {
                LEFT -> s + padding.repeat(width - s.length)
                CENTER -> {
                    val paddingCount = width - s.length
                    val pre = paddingCount / 2
                    val post = paddingCount - pre
                    padding.repeat(pre) + s + padding.repeat(post)
                }
            }
        }
    }

    class Line(
        val left: Char,
        val right: Char,
        val padding: Char,
        val content: String,
        val contentAlignment: Alignment,
        val comment: String
    ) {
        fun minWidth() = content.length

        fun format(builder: StringBuilder, minWidth: Int) {
            builder.append(left)
            builder.append(padding)
            builder.append(contentAlignment.pad(content, minWidth, padding))
            builder.append(padding)
            builder.append(right)
            builder.append(" ")
            builder.append(comment)
            builder.append("\n")
        }
    }

    var lines: MutableList<Line> = ArrayList()
    override fun toString(): String {
        val minWidth = lines.stream().mapToInt { obj: Line -> obj.minWidth() }.max().orElse(0)
        val builder = StringBuilder()
        lines.forEach { l: Line -> l.format(builder, minWidth) }
        return builder.toString()
    }

    fun line(content: String, comment: String, alignment: Alignment) {
        lines.add(Line(VERTICAL_SEP, VERTICAL_SEP, ' ', content, alignment, comment))
    }

    fun line(content: String, alignment: Alignment) {
        line(content, "", alignment)
    }

    @JvmOverloads
    fun sep(content: String, comment: String = "") {
        lines.add(Line(LEFT_CROSS, RIGHT_CROSS, HORIZONTAL_SEP, content, Alignment.CENTER, comment))
    }

    @JvmOverloads
    fun close(content: String, comment: String = "") {
        lines.add(Line(BOTTOM_LEFT_CORNER, BOTTOM_RIGHT_CORNER, HORIZONTAL_SEP, content, Alignment.CENTER, comment))
    }

    companion object {
        const val VERTICAL_SEP = '|'
        const val HORIZONTAL_SEP = '─'
        const val LEFT_CROSS = '├'
        const val RIGHT_CROSS = '┤'
        const val BOTTOM_LEFT_CORNER = '└'
        const val BOTTOM_RIGHT_CORNER = '┘'
    }
}
