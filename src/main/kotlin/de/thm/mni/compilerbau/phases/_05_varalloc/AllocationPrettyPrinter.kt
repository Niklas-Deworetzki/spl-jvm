package de.thm.mni.compilerbau.phases._05_varalloc

import de.thm.mni.compilerbau.absyn.ParameterDeclaration
import de.thm.mni.compilerbau.absyn.ProcedureDeclaration
import de.thm.mni.compilerbau.absyn.Program
import de.thm.mni.compilerbau.table.ParameterType
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.utils.AsciiGraphicalTableBuilder
import de.thm.mni.compilerbau.utils.StringOps
import de.thm.mni.compilerbau.utils.StringOps.indented
import java.util.*

internal object AllocationPrettyPrinter {
    /**
     * Formats and prints the variable allocation to a human-readable format
     * The stack layout
     *
     * @param program The abstract syntax tree of the program
     * @param table   The symbol table containing all symbols of the spl program
     */
    fun formatVars(program: Program, table: SymbolTable) {
        program.declarations.filterIsInstance<ProcedureDeclaration>()
            .forEach { procDec: ProcedureDeclaration ->
                val entry = table.lookup(procDec.name) as ProcedureEntry
                formatVarsProc(procDec, entry)
            }
    }

    private fun formatVarsProc(procDec: ProcedureDeclaration, entry: ProcedureEntry) {

        val isLeafOptimized = entry.stackLayout.isOptimizedLeafProcedure
        val varparBasis = if (isLeafOptimized) "SP" else "FP"

        val ascii = AsciiGraphicalTableBuilder()
        ascii.line("...", AsciiGraphicalTableBuilder.Alignment.CENTER)
        val zipped = IntRange(0, procDec.parameters.size - 1)
            .map { i: Int? ->
                Pair(
                    procDec.parameters[i!!],
                    Pair(
                        entry.localTable!!.lookup(procDec.parameters[i].name) as VariableEntry?,
                        entry.parameterTypes[i]
                    )
                )
            }
            .sortedWith(
                Comparator.comparing { p: Pair<ParameterDeclaration, Pair<VariableEntry?, ParameterType>> ->
                    Optional.ofNullable(p.second.first?.offset)
                        .map { o: Int? -> -o!! }
                        .orElse(Int.MIN_VALUE)
                }
            )

        for (v in zipped) {
            val consistent = v.second.first!!.offset == v.second.second.offset
            ascii.line(
                "par " + v.first.name.toString(),
                "<- $varparBasis + " +
                        if (consistent) StringOps.toString(v.second.first!!.offset)
                        else "INCONSISTENT(${StringOps.toString(v.second.first!!.offset)}/ ${StringOps.toString(v.second.second.offset)})",
                AsciiGraphicalTableBuilder.Alignment.LEFT
            )
        }

        ascii.sep("BEGIN", "<- $varparBasis")
        if (procDec.variables.isNotEmpty()) {
            val variables = procDec.variables
            val localTable = entry.localTable!!

            // process VariableDeclarations by building a sorted set of pairs of offset x name
            val vars = variables.map { v -> Pair(((localTable.lookup(v.name)!!) as VariableEntry).offset, v.name) }
                .toSortedSet(compareBy { -Optional.ofNullable(it.first).orElseGet { 0 } })

            // Vor each Variable add a line to the graph
            vars.forEach { vPair ->
                ascii.line(
                    "var ${vPair.second}",
                    "<- $varparBasis - ${(-vPair.first!!)}",
                    AsciiGraphicalTableBuilder.Alignment.LEFT
                )
            }

            if (!isLeafOptimized) ascii.sep("")
        }

        if (isLeafOptimized) ascii.close("END")
        else {
            val spString =
                kotlin.runCatching { entry.stackLayout.oldFramePointerOffset().toString() }.getOrDefault("UNKNOWN")
            ascii.line("Old FP", "<- SP + $spString", AsciiGraphicalTableBuilder.Alignment.LEFT)

            val retString =
                kotlin.runCatching { (-entry.stackLayout.oldReturnAddressOffset()).toString() }.getOrDefault("UNKNOWN")

            ascii.line("Old Return", "<- FP - $retString", AsciiGraphicalTableBuilder.Alignment.LEFT)
            if (entry.stackLayout.argumentAreaSize == null) {
                ascii.line("UNKNOWN SIZE", AsciiGraphicalTableBuilder.Alignment.LEFT)
            } else {
                ascii.sep("outgoing area")

                val maxArgs = entry.stackLayout.outgoingAreaSize!! / 4

                for (i in 0 until maxArgs) {
                    ascii.line(
                        "arg ${maxArgs - i}",
                        "<- SP + ${(maxArgs - i - 1) * 4}",
                        AsciiGraphicalTableBuilder.Alignment.LEFT
                    )
                }
            }
            ascii.sep("END", "<- SP")
            ascii.line("...", AsciiGraphicalTableBuilder.Alignment.CENTER)
        }

        println("Variable allocation for procedure '${procDec.name}':")
        println("  - size of argument area = ${StringOps.toString(entry.stackLayout.argumentAreaSize)}")
        println("  - size of localvar area = ${StringOps.toString(entry.stackLayout.localVarAreaSize)}")
        println("  - size of outgoing area = ${StringOps.toString(entry.stackLayout.outgoingAreaSize)}")
        println("  - frame size = ${entry.stackLayout.frameSize()}")
        println()
        if (isLeafOptimized) println("  Stack layout (leaf optimized):")
        else println("  Stack layout:")
        println(ascii.toString().indented(4))
        println()
    }
}