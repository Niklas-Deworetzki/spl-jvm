package de.thm.mni.compilerbau

import de.thm.mni.compilerbau.phases._01_scanner.Scanner
import de.thm.mni.compilerbau.phases._01_scanner.Token
import de.thm.mni.compilerbau.phases._01_scanner.TokenType
import de.thm.mni.compilerbau.phases._02_03_parser.Parser
import de.thm.mni.compilerbau.phases._04a_tablebuild.TableBuilder
import de.thm.mni.compilerbau.phases._04b_semant.ProcedureBodyChecker
import de.thm.mni.compilerbau.phases._05_varalloc.VarAllocator
import de.thm.mni.compilerbau.phases._06_codegen.CodeGenerator
import de.thm.mni.compilerbau.reporting.MessageFormatter
import de.thm.mni.compilerbau.utils.VersionInfo
import picocli.CommandLine
import java.io.FileOutputStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val options = CommandLineOptions()
    val cli = CommandLine(options)
    try {
        cli.parseArgs(*args)
        if (options.helpRequested) {
            cli.usage(System.out)
            exitProcess(0)
        } else if (options.versionRequested) {
            println(VersionInfo.getVersion())
            exitProcess(1)
        }
        exitProcess(run(options))
    } catch (cliException: CommandLine.ParameterException) {
        cli.usage(System.out)
        exitProcess(1)
    }
}


private fun showTokens(scanner: Scanner) {
    var token: Token
    do {
        token = scanner.nextToken()
        println(token)
    } while (token.type != TokenType.EOF)
}

private fun run(options: CommandLineOptions): Int =
    Scanner(options.inputFile, options).use { scanner ->
        if (options.debug === CommandLineOptions.DebugPhase.TOKENS) {
            showTokens(scanner)
            return 0
        }

        val parser = Parser(scanner, options)
        val program = parser.parse()
        if (program == null) {
            for (reportedError in parser.reportedErrors) {
                println(MessageFormatter.format(reportedError))
            }
            println("Syntax Error")
            return 0
        } else if (options.debug === CommandLineOptions.DebugPhase.PARSE) {
            println("Input parsed successfully!")
            return 0
        } else if (options.debug === CommandLineOptions.DebugPhase.ABSYN) {
            println(program)
            return 0
        }

        val table = TableBuilder(options).buildSymbolTable(program)
        if (options.debug == CommandLineOptions.DebugPhase.TABLES) return 0

        ProcedureBodyChecker.checkProcedures(program, table)
        if (options.debug === CommandLineOptions.DebugPhase.SEMANT) {
            println("No semantic errors found!")
            return 0
        }

        VarAllocator(options).allocVars(program, table)
        if (options.debug === CommandLineOptions.DebugPhase.VARS) return 0

        CodeGenerator(options).generateCode(program, table)
        return 0
    }


