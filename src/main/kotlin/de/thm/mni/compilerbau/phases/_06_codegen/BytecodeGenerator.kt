package de.thm.mni.compilerbau.phases._06_codegen

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.PrimitiveType
import de.thm.mni.compilerbau.utils.SplJvmDefinitions.javaTypeDescriptor
import jdk.internal.org.objectweb.asm.Opcodes.*
import org.objectweb.asm.ClassWriter

class BytecodeGenerator(val options: CommandLineOptions, val program: Program, val table: SymbolTable) {

    companion object {
        const val GENERATED_BYTECODE_VERSION: Int = V1_8
        const val GENERATED_FLAGS: Int = ACC_PUBLIC + ACC_FINAL
        const val GENERATED_CLASS_NAME: String = "Spl"

        private val ARITHMETIC_OPCODES = mapOf(
            BinaryExpression.Operator.ADD to IADD,
            BinaryExpression.Operator.SUB to ISUB,
            BinaryExpression.Operator.MUL to IMUL,
            BinaryExpression.Operator.DIV to IDIV,
        )
    }

    val classWriter = ClassWriter(0)

    fun generateCode() {
        classWriter.visit(
            GENERATED_BYTECODE_VERSION, GENERATED_FLAGS, GENERATED_CLASS_NAME,
            null, // No signature.
            Object::class.javaTypeDescriptor(), // Inherits from object.
            emptyArray() // No interfaces.
        )
        classWriter.visitSource(options.inputFile!!.name, null)

        for (procedure in program.declarations.filterIsInstance<ProcedureDeclaration>()) {
            val entry = table.lookup(procedure.name) as ProcedureEntry

            ProcedureGenerator(procedure, entry).generateCode()
        }

        classWriter.visitEnd()
    }


    private inner class ProcedureGenerator(val procedure: ProcedureDeclaration, val entry: ProcedureEntry) {
        private val scope: SymbolTable = entry.localTable
        private val methodWriter = classWriter.visitMethod(
            ACC_PRIVATE + ACC_STATIC,
            procedure.name.toString(),
            entry.javaMethodDescriptor(),
            null,
            emptyArray()
        )

        fun generateCode() {
            methodWriter.visitCode() // Start generating code.
            // TODO: Check array sizes (on demand)
            // TODO: Allocate local arrays.
            for (statement in procedure.body) {
                generateStatement(statement)
            }
            methodWriter.visitInsn(RETURN)
            methodWriter.visitEnd()
        }

        fun generateStatement(statement: Statement): Unit = when (statement) {
            is AssignStatement ->
                generateAssignStatement(statement)

            is CallStatement ->
                generateCallStatement(statement)

            is IfStatement ->
                generateIfStatement(statement)

            is WhileStatement ->
                generateWhileStatement(statement)

            is CompoundStatement ->
                statement.statements.forEach(::generateStatement)

            is EmptyStatement -> Unit
        }

        fun generateAssignStatement(statement: AssignStatement) = when (statement.target) {
            is NamedVariable -> {
                val entry = scope.lookup(statement.target.name)!! as VariableEntry

                if (entry.isReference) {
                    methodWriter.visitVarInsn(ALOAD, entry.offset)
                    TODO("Set member")
                } else {
                    generateExpression(statement.value)
                    methodWriter.visitVarInsn(ISTORE, entry.offset)
                }
            }

            is ArrayAccess -> {
                loadVariable(statement.target.array)
                generateExpression(statement.target.index)
                methodWriter.visitInsn(IASTORE)
            }
        }

        fun generateWhileStatement(statement: WhileStatement): Unit = TODO()
        fun generateIfStatement(statement: IfStatement): Unit = TODO()
        fun generateCallStatement(statement: CallStatement): Unit = TODO()


        fun generateExpression(expression: Expression): Unit = when (expression) {
            is IntLiteral ->
                generateIntLiteral(expression)

            is BinaryExpression ->
                generateBinaryExpression(expression)

            is UnaryExpression ->
                generateUnaryExpression(expression)

            is VariableExpression ->
                loadVariable(expression.variable)
        }


        fun generateIntLiteral(expression: IntLiteral) {
            methodWriter.visitLdcInsn(expression.value)
        }

        fun generateBinaryExpression(expression: BinaryExpression) {
            if (expression.operator.isArithmetic()) {
                generateExpression(expression.leftOperand)
                generateExpression(expression.rightOperand)
                methodWriter.visitInsn(ARITHMETIC_OPCODES[expression.operator]!!)
            } else TODO()
        }

        fun generateUnaryExpression(expression: UnaryExpression) {
            if (expression.operator == UnaryExpression.Operator.MINUS) {
                generateExpression(expression.operand)
                methodWriter.visitInsn(INEG)
            }
        }


        fun loadVariable(variable: Variable): Unit = when (variable) {
            is NamedVariable -> {
                val entry = scope.lookup(variable.name)!! as VariableEntry

                if (entry.isReference && entry.type is PrimitiveType) {
                    methodWriter.visitVarInsn(ALOAD, entry.offset)
                    TODO("Get member")
                } else {
                    val loadOpcode = when (entry.type) {
                        is PrimitiveType -> ILOAD
                        is ArrayType -> ALOAD
                    }
                    methodWriter.visitVarInsn(loadOpcode, entry.offset)
                }
            }

            is ArrayAccess -> {
                loadVariable(variable.array)
                generateExpression(variable.index)
                methodWriter.visitInsn(AALOAD)
            }
        }
    }

}