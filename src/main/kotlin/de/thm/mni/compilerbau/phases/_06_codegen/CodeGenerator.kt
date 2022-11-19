package de.thm.mni.compilerbau.phases._06_codegen

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.table.SymbolTable
import java.io.PrintWriter

/**
 * This class is used to generate the assembly code for the compiled program.
 * This code is emitted via the [CodePrinter] in the output field of this class.
 *
 * @param options The command line options passed to the compiler
 * @param output             The PrintWriter to the output file.
 */
class CodeGenerator(val options: CommandLineOptions, output: PrintWriter) {
    private val output: CodePrinter = CodePrinter(output)

    /**
     * Emits needed import statements, to allow usage of the predefined functions and sets the correct settings
     * for the assembler.
     */
    private fun assemblerProlog() {
        output.emitImport("printi")
        output.emitImport("printc")
        output.emitImport("readi")
        output.emitImport("readc")
        output.emitImport("exit")
        output.emitImport("time")
        output.emitImport("clearAll")
        output.emitImport("setPixel")
        output.emitImport("drawLine")
        output.emitImport("drawCircle")
        output.emitImport("_indexError")
        output.emit("")
        output.emit("\t.code")
        output.emit("\t.align\t4")
    }

    fun generateCode(program: Program, table: SymbolTable) {
        assemblerProlog()

        //TODO (assignment 6): generate eco32 assembler code for the spl program

        TODO()
    }
}
