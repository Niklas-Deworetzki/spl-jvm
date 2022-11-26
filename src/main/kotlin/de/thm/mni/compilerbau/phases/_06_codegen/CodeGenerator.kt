package de.thm.mni.compilerbau.phases._06_codegen

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.Program
import de.thm.mni.compilerbau.table.SymbolTable
import java.io.OutputStream

/**
 * This class is used to generate the assembly code for the compiled program.
 *
 * @param options The command line options passed to the compiler
 * @param output             The PrintWriter to the output file.
 */
class CodeGenerator(val options: CommandLineOptions, val output: OutputStream) {

    fun generateCode(program: Program, table: SymbolTable) {
        val bytecodeGenerator = BytecodeGenerator(options, program, table)
        bytecodeGenerator.use(BytecodeGenerator::generateCode)

        bytecodeGenerator.classWriter
    }

}
