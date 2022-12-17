package de.thm.mni.compilerbau.phases._06_codegen

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.table.ParameterType
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.PrimitiveType
import de.thm.mni.compilerbau.utils.ExtendedSyntax.asVariable
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions.javaTypeDescriptor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*
import java.util.EnumMap

class BytecodeGenerator(val options: CommandLineOptions, val program: Program, val table: SymbolTable) {

    companion object {
        const val GENERATED_BYTECODE_VERSION: Int = V1_8
        const val GENERATED_FLAGS: Int = ACC_PUBLIC + ACC_FINAL
        const val GENERATED_CLASS_NAME: String = "Spl"

        const val LIBRARY_CLASS_NAME: String = "LibSpl"

        private val ARITHMETIC_OPCODES = mapOf(
            BinaryExpression.Operator.ADD to IADD,
            BinaryExpression.Operator.SUB to ISUB,
            BinaryExpression.Operator.MUL to IMUL,
            BinaryExpression.Operator.DIV to IDIV,
        )

        private val CONDITIONAL_OPCODES = mapOf(
            BinaryExpression.Operator.EQU to IF_ICMPEQ,
            BinaryExpression.Operator.NEQ to IF_ICMPNE,
            BinaryExpression.Operator.LST to IF_ICMPLT,
            BinaryExpression.Operator.LSE to IF_ICMPLE,
            BinaryExpression.Operator.GRT to IF_ICMPGT,
            BinaryExpression.Operator.GRE to IF_ICMPGE,
        )

        private val NEGATED_COMPARATORS = run {
            val inverses = listOf(
                BinaryExpression.Operator.EQU to BinaryExpression.Operator.NEQ,
                BinaryExpression.Operator.LST to BinaryExpression.Operator.GRE,
                BinaryExpression.Operator.LSE to BinaryExpression.Operator.GRT,
            )

            val enumClass = BinaryExpression.Operator::class.java
            val result = EnumMap<BinaryExpression.Operator, BinaryExpression.Operator>(enumClass)
            for (pair in inverses) {
                result[pair.first] = pair.second
                result[pair.second] = pair.first
            }
            result
        }
    }

    fun thisClassDescriptor(): String = GENERATED_CLASS_NAME


    val classWriter = ClassWriter(0)

    fun generateCode() {
        classWriter.visit(
            GENERATED_BYTECODE_VERSION, GENERATED_FLAGS, thisClassDescriptor(),
            null, // No signature.
            Object::class.javaTypeDescriptor(), // Inherits from object.
            emptyArray() // No interfaces.
        )
        classWriter.visitSource(options.inputFile.name, null)

        for (procedure in program.declarations.filterIsInstance<ProcedureDeclaration>()) {
            val entry = table.lookup(procedure.name) as ProcedureEntry

            ProcedureGenerator(procedure, entry).generateCode()
        }

        classWriter.visitEnd()
    }


    private inner class ProcedureGenerator(val procedure: ProcedureDeclaration, val entry: ProcedureEntry) {
        private val scope = entry.localTable
        private val layout = entry.stackLayout
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
            // TODO: Reference pool.
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

        fun generateAssignStatement(statement: AssignStatement) =
            storeVariable(statement.target) { generateExpression(statement.value) }

        fun generateWhileStatement(statement: WhileStatement) =
            if (statement.condition is BinaryExpression && statement.condition.isSimpleComparison()) {
                val beforeBody = Label()
                val beforeCondition = Label()

                methodWriter.visitJumpInsn(GOTO, beforeCondition)
                run {
                    methodWriter.visitLabel(beforeBody)
                    generateStatement(statement.body)
                }
                methodWriter.visitLabel(beforeCondition)
                generateCondition(
                    statement.condition.lhs,
                    statement.condition.operator,
                    statement.condition.rhs,
                    beforeBody
                )
            } else TODO()

        fun generateIfStatement(statement: IfStatement) =
            if (statement.condition is BinaryExpression && statement.condition.isSimpleComparison()) {
                val beforeElse = Label()
                val afterElse = Label()

                generateCondition(
                    statement.condition.lhs,
                    NEGATED_COMPARATORS[statement.condition.operator]!!,
                    statement.condition.rhs,
                    beforeElse
                )
                run {
                    generateStatement(statement.thenPart)
                    methodWriter.visitJumpInsn(GOTO, afterElse)
                }
                run {
                    methodWriter.visitLabel(beforeElse)
                    generateStatement(statement.elsePart)
                    methodWriter.visitLabel(afterElse)
                }
            } else TODO()

        fun generateCallStatement(statement: CallStatement) {
            val targetEntry = scope.upperLevel?.lookupAs<ProcedureEntry>(statement.procedureName)!!

            val promotedReferenceArguments = mutableListOf<Argument>()
            for (argument in statement.arguments) {
                when (argument.passingMode) {
                    is Argument.ByValue -> // Simply evaluate argument.
                        generateExpression(argument.value)

                    is Argument.ByReferenceArray -> // Places array as reference on stack.
                        loadVariable(argument.asVariable())

                    is Argument.ByReferenceInteger -> { // Argument is already reference.
                        val variable = argument.asVariable() as NamedVariable
                        methodWriter.visitVarInsn(ALOAD, scope.lookupAs<VariableEntry>(variable.name).offset)
                    }

                    is Argument.PromoteToReference -> {
                        promotedReferenceArguments.add(argument)
                        val referencePoolIndex =
                            (argument.passingMode as Argument.PromoteToReference).referencePoolIndex

                        generateExpression(argument.value) // Evaluate argument
                        referenceSet(layout.poolIndexToOffset(referencePoolIndex)) // Store in reference from pool
                        methodWriter.visitVarInsn(ALOAD, layout.poolIndexToOffset(referencePoolIndex)) // Get from pool
                    }
                }
            }

            methodWriter.visitMethodInsn(
                INVOKESTATIC,
                if (targetEntry.isInternal) LIBRARY_CLASS_NAME else thisClassDescriptor(),
                statement.procedureName.toString(),
                targetEntry.javaMethodDescriptor(),
                false // Is not defined on interface.
            )

            // Update values of promoted arguments.
            for (referenceArgument in promotedReferenceArguments) {
                val referencePoolIndex =
                    (referenceArgument.passingMode as Argument.PromoteToReference).referencePoolIndex

                storeVariable(referenceArgument.asVariable()) {
                    referenceGet(layout.poolIndexToOffset(referencePoolIndex))
                }
            }
        }


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
                generateExpression(expression.lhs)
                generateExpression(expression.rhs)
                methodWriter.visitInsn(ARITHMETIC_OPCODES[expression.operator]!!)
            }
        }

        fun generateUnaryExpression(expression: UnaryExpression) {
            if (expression.operator == UnaryExpression.Operator.MINUS) {
                generateExpression(expression.operand)
                methodWriter.visitInsn(INEG)
            }
        }


        fun loadVariable(variable: Variable): Unit = when (variable) {
            is NamedVariable -> {
                val entry = scope.lookupAs<VariableEntry>(variable.name)

                if (entry.isReferenceInteger()) {
                    referenceGet(entry.offset)
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

        fun storeVariable(variable: Variable, generateStoredValue: () -> Unit) {
            when (variable) {
                is NamedVariable -> {
                    val entry = scope.lookupAs<VariableEntry>(variable.name)

                    generateStoredValue()
                    if (entry.isReference) {
                        referenceSet(entry.offset)
                    } else {
                        methodWriter.visitVarInsn(ISTORE, entry.offset)
                    }
                }

                is ArrayAccess -> {
                    loadVariable(variable.array)
                    generateExpression(variable.index)
                    generateStoredValue()
                    methodWriter.visitInsn(IASTORE)
                }
            }
        }

        fun referenceGet(offset: Int) {
            methodWriter.visitVarInsn(ALOAD, offset)
            methodWriter.visitMethodInsn(
                INVOKEVIRTUAL,
                SplJvmDefinitions.REFERENCE_INTEGER_CLASS,
                SplJvmDefinitions.REFERENCE_INTEGER_METHOD_GET.name,
                SplJvmDefinitions.REFERENCE_INTEGER_METHOD_GET.descriptor,
                false
            )
        }

        fun referenceSet(offset: Int) {
            methodWriter.visitVarInsn(ALOAD, offset)
            methodWriter.visitInsn(SWAP)
            methodWriter.visitMethodInsn(
                INVOKEVIRTUAL,
                SplJvmDefinitions.REFERENCE_INTEGER_CLASS,
                SplJvmDefinitions.REFERENCE_INTEGER_METHOD_SET.name,
                SplJvmDefinitions.REFERENCE_INTEGER_METHOD_SET.descriptor,
                false
            )
        }


        fun generateCondition(
            lhs: Expression,
            comparator: BinaryExpression.Operator,
            rhs: Expression,
            jumpOnTrue: Label
        ) {
            generateExpression(lhs)
            generateExpression(rhs)
            methodWriter.visitJumpInsn(CONDITIONAL_OPCODES[comparator]!!, jumpOnTrue)
        }
    }

    private fun BinaryExpression.isSimpleComparison(): Boolean =
        this.lhs.dataType == PrimitiveType.Int && this.rhs.dataType == PrimitiveType.Int

    private fun VariableEntry.isReferenceInteger(): Boolean =
        this.isReference && this.type is PrimitiveType
}