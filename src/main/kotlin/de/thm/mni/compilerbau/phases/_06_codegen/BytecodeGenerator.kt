package de.thm.mni.compilerbau.phases._06_codegen

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.jvm.JavaTypeDescriptors.javaMethodDescriptor
import de.thm.mni.compilerbau.jvm.JavaTypeDescriptors.javaTypeDescriptor
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions.GENERATED_CLASS_NAME
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions.LIBRARY_CLASS_NAME
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions.REFERENCE_INTEGER_CLASS_DESCRIPTOR
import de.thm.mni.compilerbau.jvm.SplJvmDefinitions.javaInternalName
import de.thm.mni.compilerbau.phases._06_codegen.OptimizingIntegerPush.push
import de.thm.mni.compilerbau.table.Identifier.Companion.IDENTIFIER_MAIN
import de.thm.mni.compilerbau.table.ProcedureEntry
import de.thm.mni.compilerbau.table.SymbolTable
import de.thm.mni.compilerbau.table.VariableEntry
import de.thm.mni.compilerbau.types.ArrayType
import de.thm.mni.compilerbau.types.PrimitiveType
import de.thm.mni.compilerbau.utils.ExtendedSyntax.asVariable
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*
import java.util.*

class BytecodeGenerator(private val options: CommandLineOptions, val program: Program, val table: SymbolTable) {

    companion object {
        const val GENERATED_BYTECODE_VERSION: Int = V1_8
        const val GENERATED_FLAGS: Int = ACC_PUBLIC + ACC_FINAL

        private const val ASM_AUTO_COMPUTE = -1

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

    private fun procedureVisibility(procedure: ProcedureDeclaration): Int = when {
        procedure.name == IDENTIFIER_MAIN -> ACC_PUBLIC // Main must always be visible as program entry point.
        options.makePublic -> ACC_PUBLIC
        else -> ACC_PRIVATE
    }

    val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)

    fun generateCode() {
        classWriter.visit(
            GENERATED_BYTECODE_VERSION, GENERATED_FLAGS, GENERATED_CLASS_NAME,
            null, // No signature.
            Object::class.javaInternalName(), // Inherits from object.
            emptyArray() // No interfaces.
        )
        classWriter.visitSource(options.inputFile.name, null)

        for (procedure in program.declarations.filterIsInstance<ProcedureDeclaration>()) {
            val entry = table.lookup(procedure.name) as ProcedureEntry
            ProcedureGenerator(procedure, entry).generateCode()
        }

        classWriter.visitEnd()
    }


    private inner class ProcedureGenerator(val procedure: ProcedureDeclaration, entry: ProcedureEntry) {
        private val scope = entry.localTable
        private val layout = entry.stackLayout

        private val methodWriter = classWriter.visitMethod(
            procedureVisibility(procedure) + ACC_STATIC,
            procedure.name.toString(),
            entry.javaMethodDescriptor(),
            null, // No signature.
            emptyArray() // Don't throw exceptions.
        )

        // Labels used to mark variable lifetime in debug info.
        private val beginOfProcedure = Label()
        private val endOfProcedure = Label()
        private val endOfPrologue = Label()

        fun generateCode() {
            methodWriter.visitCode() // Start generating code.

            methodWriter.visitLabel(beginOfProcedure)
            generateLocalVariableInitializers()
            generateMethodBody()
            methodWriter.visitInsn(RETURN)
            methodWriter.visitLabel(endOfProcedure)

            methodWriter.visitMaxs(ASM_AUTO_COMPUTE, ASM_AUTO_COMPUTE)
            generateDebugInformation()
            methodWriter.visitEnd()
        }

        private fun generateLocalVariableInitializers() {
            val initializer = VariableInitializer(methodWriter)
            initializer.initializeReferencePool(layout)
            for (variable in procedure.variables) {
                val entry = scope.lookupAs<VariableEntry>(variable.name)
                initializer.initialize(entry)
            }
            methodWriter.visitLabel(endOfPrologue)
        }

        private fun generateMethodBody() {
            for (statement in procedure.body) {
                generateStatement(statement)
            }
        }

        private fun generateDebugInformation() {
            for (parameter in procedure.parameters) {
                val entry = scope.lookupAs<VariableEntry>(parameter.name)
                methodWriter.visitLocalVariable(
                    parameter.name.toString(),
                    entry.type.javaTypeDescriptor(),
                    null,
                    beginOfProcedure,
                    endOfProcedure,
                    entry.offset
                )
            }
            for (variable in procedure.variables) {
                val entry = scope.lookupAs<VariableEntry>(variable.name)
                methodWriter.visitLocalVariable(
                    variable.name.toString(),
                    entry.type.javaTypeDescriptor(),
                    null,
                    beginOfProcedure,
                    endOfProcedure,
                    entry.offset
                )
            }
            for (index in 0 until layout.referencePoolSize) {
                methodWriter.visitLocalVariable(
                    "\$$index",
                    REFERENCE_INTEGER_CLASS_DESCRIPTOR,
                    null,
                    beginOfProcedure,
                    endOfPrologue,
                    layout.poolIndexToOffset(index)
                )
            }
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

        fun generateWhileStatement(statement: WhileStatement) {
            val beforeBody = Label()
            val beforeCondition = Label()

            methodWriter.visitJumpInsn(GOTO, beforeCondition)
            run {
                methodWriter.visitLabel(beforeBody)
                generateStatement(statement.body)
            }
            methodWriter.visitLabel(beforeCondition)
            generateCondition(statement.condition as BinaryExpression, beforeBody)
        }

        fun generateIfStatement(statement: IfStatement) {
            val beforeElse = Label()
            val afterElse = Label()

            generateCondition(statement.condition as BinaryExpression, beforeElse, negateOperator = true)
            run {
                generateStatement(statement.thenPart)
                methodWriter.visitJumpInsn(GOTO, afterElse)
            }
            run {
                methodWriter.visitLabel(beforeElse)
                generateStatement(statement.elsePart)
                methodWriter.visitLabel(afterElse)
            }
        }

        fun generateCallStatement(statement: CallStatement) {
            val promotedReferenceArguments = prepareArguments(statement)

            val targetEntry = scope.upperLevel?.lookupAs<ProcedureEntry>(statement.procedureName)!!
            methodWriter.visitMethodInsn(
                INVOKESTATIC,
                if (targetEntry.isInternal) LIBRARY_CLASS_NAME else GENERATED_CLASS_NAME,
                statement.procedureName.toString(),
                targetEntry.javaMethodDescriptor(),
                false // Is not defined on interface.
            )

            updatePromotedReferenceArguments(promotedReferenceArguments)
        }

        private fun prepareArguments(statement: CallStatement): List<Argument> {
            val promotedReferenceArguments = mutableListOf<Argument>()
            for (argument in statement.arguments) {
                when (argument.passingMode) {
                    is Argument.ByIntegerValue -> // Simply evaluate argument.
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

                        referenceSet(layout.poolIndexToOffset(referencePoolIndex)) { // Update reference in pool.
                            generateExpression(argument.value)
                        }
                        // Get initialized reference from pool
                        methodWriter.visitVarInsn(ALOAD, layout.poolIndexToOffset(referencePoolIndex))
                    }
                }
            }
            return promotedReferenceArguments
        }

        private fun updatePromotedReferenceArguments(promotedReferenceArguments: List<Argument>) {
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
            methodWriter.push(expression.value)
        }

        fun generateBinaryExpression(expression: BinaryExpression) {
            generateExpression(expression.lhs)
            generateExpression(expression.rhs)
            methodWriter.visitInsn(ARITHMETIC_OPCODES[expression.operator]!!)
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
                when (variable.dataType) {
                    is ArrayType -> methodWriter.visitInsn(AALOAD)
                    else -> methodWriter.visitInsn(IALOAD)
                }
            }
        }

        fun storeVariable(variable: Variable, generateStoredValue: () -> Unit) {
            when (variable) {
                is NamedVariable -> {
                    val entry = scope.lookupAs<VariableEntry>(variable.name)

                    if (entry.isReference) {
                        referenceSet(entry.offset, generateStoredValue)
                    } else {
                        generateStoredValue()
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
            methodWriter.visitFieldInsn(
                GETFIELD,
                SplJvmDefinitions.REFERENCE_INTEGER_CLASS_NAME,
                SplJvmDefinitions.REFERENCE_INTEGER_VALUE_NAME,
                SplJvmDefinitions.REFERENCE_INTEGER_VALUE_DESCRIPTOR,
            )
        }

        fun referenceSet(offset: Int, generateStoredValue: () -> Unit) {
            methodWriter.visitVarInsn(ALOAD, offset)
            generateStoredValue()
            methodWriter.visitFieldInsn(
                PUTFIELD,
                SplJvmDefinitions.REFERENCE_INTEGER_CLASS_NAME,
                SplJvmDefinitions.REFERENCE_INTEGER_VALUE_NAME,
                SplJvmDefinitions.REFERENCE_INTEGER_VALUE_DESCRIPTOR,
            )
        }


        fun generateCondition(
            expression: BinaryExpression,
            jumpOnTrue: Label,
            negateOperator: Boolean = false
        ) {
            generateExpression(expression.lhs)
            generateExpression(expression.rhs)

            val comparator = when {
                negateOperator -> NEGATED_COMPARATORS[expression.operator]!!
                else -> expression.operator
            }
            methodWriter.visitJumpInsn(CONDITIONAL_OPCODES[comparator]!!, jumpOnTrue)
        }
    }

    private fun VariableEntry.isReferenceInteger(): Boolean =
        this.isReference && this.type is PrimitiveType
}