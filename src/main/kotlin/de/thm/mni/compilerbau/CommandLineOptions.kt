package de.thm.mni.compilerbau

import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.io.File

class CommandLineOptions {
    @Parameters(
        index = "0",
        arity = "1",
        description = ["The input file containing the source program."]
    )
    lateinit var inputFile: File

    @Parameters(
        index = "1",
        arity = "0..1"
    )
    var outputFile: File? = null

    @Option(
        names = ["-h", "--help"],
        description = ["Displays this help message."]
    )
    var helpRequested: Boolean = false

    @Option(
        names = ["-v", "--version"],
        description = ["Displays the compiler version."]
    )
    var versionRequested: Boolean = false

    enum class DebugPhase {
        TOKENS, PARSE, ABSYN, TABLES, SEMANT, VARS
    }

    @Option(
        names = ["--debug", "--debug-pass"],
        description = ["Only executes up to the given pass and produces debug output.",
            "Supported values are: \${COMPLETION-CANDIDATES}"]
    )
    var debug: DebugPhase? = null

    var ershovOptimization = false
    var doWhileEnabled = false
    var firstClassBoolean = false
    var nestedScopesEnabled = false

}
