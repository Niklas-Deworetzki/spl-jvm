package de.thm.mni.compilerbau.position

import java.io.File
import kotlin.math.max
import kotlin.math.min

data class Range(val file: File, val offsetStart: Long, val offsetEnd: Long) {


    fun union(range: Range): Range =
        Range(file, min(this.offsetStart, range.offsetStart), max(this.offsetEnd, range.offsetEnd))
}