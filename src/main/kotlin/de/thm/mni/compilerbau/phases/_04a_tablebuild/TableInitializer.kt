package de.thm.mni.compilerbau.phases._04a_tablebuild

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.Constants
import de.thm.mni.compilerbau.table.*
import de.thm.mni.compilerbau.types.PrimitiveType

internal object TableInitializer {
    /**
     * Creates a new SymbolTable and enters entries for all predefined types and procedures.
     *
     * @return A new instance of the symbol table representing the global definition scope.
     */
    fun initializeGlobalTable(options: CommandLineOptions): SymbolTable {
        val table = SymbolTable()
        enterPredefinedTypes(table, options)
        enterPredefinedProcedures(table, options)
        return table
    }

    private fun enterPredefinedTypes(table: SymbolTable, options: CommandLineOptions) {
        table.enter(Identifier("int"), TypeEntry(PrimitiveType.intType))
    }

    private fun enterPredefinedProcedures(table: SymbolTable, options: CommandLineOptions) {
        // printi(i: int)
        table.enter(
            Identifier("printi"),
            ProcedureEntry.predefinedProcedureEntry(
                listOf(ParameterType(PrimitiveType.intType, false, 0)),
                PrimitiveType.intType.byteSize
            )
        )

        // printc(i: int)
        table.enter(
            Identifier("printc"),
            ProcedureEntry.predefinedProcedureEntry(
                listOf(ParameterType(PrimitiveType.intType, false, 0)),
                PrimitiveType.intType.byteSize
            )
        )
        // readi(ref i: int)
        table.enter(
            Identifier("readi"),
            ProcedureEntry.predefinedProcedureEntry(
                listOf(ParameterType(PrimitiveType.intType, true, 0)),
                Constants.REFERENCE_BYTESIZE
            )
        )
        // readc(ref i: int)
        table.enter(
            Identifier("readc"),
            ProcedureEntry.predefinedProcedureEntry(
                listOf(ParameterType(PrimitiveType.intType, true, 0)),
                Constants.REFERENCE_BYTESIZE
            )
        )

        // exit()
        table.enter(
            Identifier("exit"),
            ProcedureEntry.predefinedProcedureEntry(listOf(), 0)
        )

        // time(ref i: int)
        table.enter(
            Identifier("time"),
            ProcedureEntry.predefinedProcedureEntry(
                listOf(ParameterType(PrimitiveType.intType, true, 0)),
                Constants.REFERENCE_BYTESIZE
            )
        )
        // clearAll(color: int)
        table.enter(
            Identifier("clearAll"),
            ProcedureEntry.predefinedProcedureEntry(
                listOf(ParameterType(PrimitiveType.intType, false, 0)),
                PrimitiveType.intType.byteSize
            )
        )
        // setPixel(x: int, y: int, color: int)
        table.enter(
            Identifier("setPixel"),
            ProcedureEntry.predefinedProcedureEntry(
                listOf(
                    ParameterType(PrimitiveType.intType, false, 0),
                    ParameterType(PrimitiveType.intType, false, PrimitiveType.intType.byteSize),
                    ParameterType(PrimitiveType.intType, false, 2 * PrimitiveType.intType.byteSize)
                ),
                3 * PrimitiveType.intType.byteSize
            )
        )
        // drawLine(x1: int, y1: int, x2: int, y2: int, color: int)
        table.enter(
            Identifier("drawLine"),
            ProcedureEntry.predefinedProcedureEntry(
                listOf(
                    ParameterType(PrimitiveType.intType, false, 0),
                    ParameterType(PrimitiveType.intType, false, PrimitiveType.intType.byteSize),
                    ParameterType(PrimitiveType.intType, false, 2 * PrimitiveType.intType.byteSize),
                    ParameterType(PrimitiveType.intType, false, 3 * PrimitiveType.intType.byteSize),
                    ParameterType(PrimitiveType.intType, false, 4 * PrimitiveType.intType.byteSize)
                ),
                5 * PrimitiveType.intType.byteSize
            )
        )
        // drawCircle(x0: int, y0: int, radius: int, color: int)
        table.enter(
            Identifier("drawCircle"),
            ProcedureEntry.predefinedProcedureEntry(
                listOf(
                    ParameterType(PrimitiveType.intType, false, 0),
                    ParameterType(PrimitiveType.intType, false, PrimitiveType.intType.byteSize),
                    ParameterType(PrimitiveType.intType, false, 2 * PrimitiveType.intType.byteSize),
                    ParameterType(PrimitiveType.intType, false, 3 * PrimitiveType.intType.byteSize)
                ),
                4 * PrimitiveType.intType.byteSize
            )
        )
    }
}
