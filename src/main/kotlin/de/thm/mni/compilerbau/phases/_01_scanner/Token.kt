package de.thm.mni.compilerbau.phases._01_scanner

import de.thm.mni.compilerbau.position.Range

data class Token(val range: Range, val type: TokenType, val value: Any? = null) {

    override fun toString(): String =
        if (value == null) type.toString()
        else "$type($value)"
}
