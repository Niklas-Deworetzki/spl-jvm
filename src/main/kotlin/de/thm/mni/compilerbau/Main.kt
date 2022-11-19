package de.thm.mni.compilerbau

import de.thm.mni.compilerbau.absyn.Program
import de.thm.mni.compilerbau.phases._01_scanner.Scanner
import de.thm.mni.compilerbau.phases._02_03_parser.Parser
import de.thm.mni.compilerbau.phases._02_03_parser.Sym
import de.thm.mni.compilerbau.phases._04a_tablebuild.TableBuilder
import de.thm.mni.compilerbau.phases._04b_semant.ProcedureBodyChecker
import de.thm.mni.compilerbau.phases._05_varalloc.VarAllocator
import de.thm.mni.compilerbau.phases._06_codegen.CodeGenerator
import de.thm.mni.compilerbau.table.Identifier
import de.thm.mni.compilerbau.utils.SplError
import java_cup.runtime.DefaultSymbolFactory
import java_cup.runtime.Symbol
import java_cup.runtime.SymbolFactory
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import kotlin.system.exitProcess

private fun showToken(token: Symbol) {
    print("TOKEN = ${Sym.terminalNames[token.sym]}") // Name of token class
    if (token.sym != Sym.EOF)
        print(" in line ${token.left}, column ${token.right}") // Line and Column
    if (token.value != null) {
        print(", value = ")
        if (token.value is String || token.value is Identifier)
            print("\"${token.value}\"")
        else print(token.value)
    }
    println()
}

/**
 * CUP encourages you to use {@link java_cup.runtime.ComplexSymbolFactory} as a {@link SymbolFactory} which we
 * don't need. The default implementation provides more than enough information for our needs.
 */
@Suppress("DEPRECATION")
var symbolFactory: SymbolFactory = DefaultSymbolFactory()

fun main(args: Array<String>) {
    val options = CommandLineOptions.parse(args)
    try {
        val source = FileReader(options.inFilename)
        val scanner = Scanner(source)
        scanner.options = options
        if (options.phaseOption === CommandLineOptions.PhaseOption.TOKENS) {
            var token: Symbol
            do {
                token = scanner.next_token()
                showToken(token)
            } while (token.sym != Sym.EOF)
            exitProcess(0)
        }
        val parser = Parser(scanner, symbolFactory)
        parser.options =
                options // Inject the command line options into the parser to grant it access to feature flags.

        val program =
                parser.parse().value as Program // Change 'parse' to 'debug_parse' for detailed parsing output. Don't forget to change it back

        if (options.phaseOption === CommandLineOptions.PhaseOption.PARSE) {
            println("Input parsed successfully!")
            exitProcess(0)
        }

        if (options.phaseOption === CommandLineOptions.PhaseOption.ABSYN) {
            println(program)
            exitProcess(0)
        }

        val table = TableBuilder(options).buildSymbolTable(program)
        if (options.phaseOption == CommandLineOptions.PhaseOption.TABLES) exitProcess(0)

        ProcedureBodyChecker.checkProcedures(program, table)
        if (options.phaseOption === CommandLineOptions.PhaseOption.SEMANT) {
            println("No semantic errors found!")
            exitProcess(0)
        }

        VarAllocator(options).allocVars(program, table)
        if (options.phaseOption === CommandLineOptions.PhaseOption.VARS) exitProcess(0)

        try {
            options.outputWriter.use { out ->
                CodeGenerator(options, out).generateCode(
                        program,
                        table
                )
            }
        } catch (e: IOException) {
            System.err.println("An error occurred: Cannot open output file '${options.outFilename}'")
            exitProcess(1)
        }
    } catch (e: FileNotFoundException) {
        System.err.println("An error occurred: Cannot open input file '${options.inFilename}'")
        exitProcess(1)
    } catch (error: SplError) {
        System.err.println("An error occurred:")
        if (error.position.line >= 0) System.err.printf(
                "Line %d, Column %d: ",
                error.position.line,
                error.position.column
        )
        System.err.println(error.message)
        exitProcess(error.errorCode)
    } catch (e: Exception) {
        System.err.println("An error occurred: " + e.message)
        e.printStackTrace()
        exitProcess(1)
    }
}



