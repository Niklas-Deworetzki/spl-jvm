package de.thm.mni.compilerbau.phases._04a_tablebuild

import de.thm.mni.compilerbau.table.*
import de.thm.mni.compilerbau.types.PrimitiveType

internal object TableInitializer {
    /**
     * Creates a new SymbolTable and enters entries for all predefined types and procedures.
     *
     * @return A new instance of the symbol table representing the global definition scope.
     */
    fun initializeGlobalTable(): SymbolTable {
        val table = SymbolTable()
        enterPredefinedTypes(table)
        enterPredefinedProcedures(table)
        return table
    }

    private fun enterPredefinedTypes(table: SymbolTable) {
        table.enter(Identifier("int"), TypeEntry(PrimitiveType.Int))
    }

    private fun enterPredefinedProcedures(table: SymbolTable) {
        // printi(i: int)
        table.enter(
            Identifier("printi"),
            ProcedureEntry.predefinedProcedureEntry(
                ParameterType(PrimitiveType.Int, false)
            )
        )

        // printc(i: int)
        table.enter(
            Identifier("printc"),
            ProcedureEntry.predefinedProcedureEntry(
                ParameterType(PrimitiveType.Int, false)
            )
        )
        // readi(ref i: int)
        table.enter(
            Identifier("readi"),
            ProcedureEntry.predefinedProcedureEntry(
                ParameterType(PrimitiveType.Int, true)
            )
        )
        // readc(ref i: int)
        table.enter(
            Identifier("readc"),
            ProcedureEntry.predefinedProcedureEntry(
                ParameterType(PrimitiveType.Int, true)
            )
        )

        // exit()
        table.enter(
            Identifier("exit"),
            ProcedureEntry.predefinedProcedureEntry()
        )

        // time(ref i: int)
        table.enter(
            Identifier("time"),
            ProcedureEntry.predefinedProcedureEntry(
                ParameterType(PrimitiveType.Int, true)
            )
        )
        // clearAll(color: int)
        table.enter(
            Identifier("clearAll"),
            ProcedureEntry.predefinedProcedureEntry(
                ParameterType(PrimitiveType.Int, false)
            )
        )
        // setPixel(x: int, y: int, color: int)
        table.enter(
            Identifier("setPixel"),
            ProcedureEntry.predefinedProcedureEntry(
                ParameterType(PrimitiveType.Int, false),
                ParameterType(PrimitiveType.Int, false),
                ParameterType(PrimitiveType.Int, false)
            )
        )
        // drawLine(x1: int, y1: int, x2: int, y2: int, color: int)
        table.enter(
            Identifier("drawLine"),
            ProcedureEntry.predefinedProcedureEntry(
                ParameterType(PrimitiveType.Int, false),
                ParameterType(PrimitiveType.Int, false),
                ParameterType(PrimitiveType.Int, false),
                ParameterType(PrimitiveType.Int, false),
                ParameterType(PrimitiveType.Int, false)
            )
        )
        // drawCircle(x0: int, y0: int, radius: int, color: int)
        table.enter(
            Identifier("drawCircle"),
            ProcedureEntry.predefinedProcedureEntry(
                ParameterType(PrimitiveType.Int, false),
                ParameterType(PrimitiveType.Int, false),
                ParameterType(PrimitiveType.Int, false),
                ParameterType(PrimitiveType.Int, false)
            )
        )
    }
}
