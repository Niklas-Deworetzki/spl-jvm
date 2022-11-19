package de.thm.mni.compilerbau.phases._06_codegen

import java.io.PrintWriter

class CodePrinter(private val outputFile: PrintWriter) {

    fun emitInstruction(opcode: String, r1: Register, r2: Register, r3: Register) =
        outputFile.println("\t$opcode\t$r1,$r2,$r3")

    fun emitInstruction(opcode: String, r1: Register, r2: Register, value: Int) =
        outputFile.println("\t$opcode\t$r1,$r2,$value")

    fun emitInstruction(opcode: String, r1: Register, r2: Register, label: String?) =
        outputFile.println("\t$opcode\t$r1,$r2,$label")

    fun emitInstruction(opcode: String, r1: Register) = outputFile.println("\t$opcode\t$r1")

    fun emitInstruction(opcode: String, label: String) = outputFile.println("\t$opcode\t$label")

    fun emitInstruction(opcode: String, r1: Register, r2: Register, r3: Register, comment: String) =
        outputFile.println("\t$opcode\t$r1,$r2,$r3\t\t; $comment")

    fun emitInstruction(opcode: String, r1: Register, r2: Register, value: Int, comment: String) {
        outputFile.println("\t$opcode\t$r1,$r2,$value\t\t; $comment")
    }

    fun emitInstruction(opcode: String?, r1: Register, r2: Register, label: String, comment: String) =
        outputFile.println("\t$opcode\t$r1,$r2,$label\t\t; $comment")

    fun emitInstruction(opcode: String, r1: Register, comment: String) =
        outputFile.println("\t$opcode\t$r1\t\t\t; $comment")

    fun emitInstruction(opcode: String, label: String, comment: String) =
        outputFile.println("\t$opcode\t$label\t\t\t; $comment")

    fun emitLabel(label: String) = outputFile.println("$label:")

    fun emitImport(label: String) = outputFile.println("\t.import\t$label")

    fun emitExport(label: String) = outputFile.println("\t.export\t$label")

    fun emit(str: String) = outputFile.println(str)
}
