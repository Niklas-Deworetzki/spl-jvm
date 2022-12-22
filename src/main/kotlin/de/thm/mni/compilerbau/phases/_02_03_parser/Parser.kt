package de.thm.mni.compilerbau.phases._02_03_parser

import de.thm.mni.compilerbau.absyn.*
import de.thm.mni.compilerbau.absyn.Node.Companion.withPosition
import de.thm.mni.compilerbau.phases._01_scanner.Scanner
import de.thm.mni.compilerbau.phases._01_scanner.TokenType
import de.thm.mni.compilerbau.phases._01_scanner.TokenType.*
import de.thm.mni.compilerbau.table.Identifier

class Parser(scanner: Scanner) : AbstractParser<Program>(scanner) {

    override fun describeTokenType(tokenType: TokenType): String = tokenType.toString()

    override fun parserEntry(): Program = parseProgram()

    private fun parseProgram(): Program {
        beginParsing()
        val declarations = parseRepeated(this::parseGlobalDeclaration, setOf(TYPE, PROC), setOf(EOF))
        return finishParsing(Program(declarations))
    }

    private fun parseGlobalDeclaration(): GlobalDeclaration = when (lookahead.type) {
        TYPE -> {
            beginParsing()
            advance()

            val identifier = parseIdentifier()
            token(OP_EQ)
            val typeExpression = parseTypeExpression()
            token(SEMIC)

            finishParsing(TypeDeclaration(identifier, typeExpression))
        }

        PROC -> {
            beginParsing()
            advance()

            val identifier = parseIdentifier()
            token(PAR_L)
            val parameters = parseSepList(COMMA, PAR_R, ::parseParameterDeclaration)

            token(CUR_L)
            val variables = mutableListOf<VariableDeclaration>()
            while (lookahead.type === VAR)
                variables.add(parseVariableDeclaration())
            val statements = mutableListOf<Statement>()
            while (lookahead.type !== CUR_R)
                statements.add(parseStatement())
            token(CUR_R)

            finishParsing(ProcedureDeclaration(identifier, parameters, variables, statements))
        }

        else ->
            error(lookahead.range, "global declaration")
    }


    private fun parseIdentifier(): Identifier =
        Identifier(tokenAs(IDENTIFIER, "identifier"))

    private fun parseTypeExpression(): TypeExpression = when (lookahead.type) {
        ARRAY -> {
            beginParsing()
            advance()
            token(BRA_L)
            val size = tokenAs<Int>(INTEGER, "array size")
            token(BRA_R)
            token(OF)
            val base = parseTypeExpression()
            finishParsing(ArrayTypeExpression(size, base))
        }

        IDENTIFIER -> {
            beginParsing()
            val typename = parseIdentifier()
            finishParsing(NamedTypeExpression(typename))
        }

        else ->
            error(lookahead.range, "type expression")
    }

    private fun parseParameterDeclaration(): ParameterDeclaration {
        beginParsing()
        val isReference = isPresent(REF)
        val name = parseIdentifier()
        token(COLON)
        val type = parseTypeExpression()
        return finishParsing(ParameterDeclaration(name, type, isReference))
    }

    private fun parseVariableDeclaration(): VariableDeclaration {
        beginParsing()
        token(VAR)
        val name = parseIdentifier()
        token(COLON)
        val type = try {
            val result = parseTypeExpression()
            token(SEMIC)
            result
        } catch (recovery: Recovery) {
            reentryAt(setOf(SEMIC, CUR_R))
            if (lookahead.type === SEMIC) advance()
            beginParsing() // Restart parsing.
            NamedTypeExpression(Identifier("Int"))
        }
        return finishParsing(VariableDeclaration(name, type))
    }

    private fun parseStatement(): Statement = when (lookahead.type) {
        CUR_L -> {
            beginParsing()
            advance()
            val statements = parseRepeated(::parseStatement, emptySet(), setOf(CUR_R))
            advance()
            finishParsing(CompoundStatement(statements))
        }

        IF -> {
            beginParsing()
            advance()
            token(PAR_L)
            val condition = parseExpression()
            token(PAR_R)
            val thenBranch = parseStatement()
            val elseBranch = if (isPresent(ELSE)) parseStatement() else EmptyStatement().withPosition(current.range)
            finishParsing(IfStatement(condition, thenBranch, elseBranch))
        }

        WHILE -> {
            beginParsing()
            advance()
            token(PAR_L)
            val condition = parseExpression()
            token(PAR_R)
            val body = parseStatement()
            finishParsing(WhileStatement(condition, body))
        }

        IDENTIFIER -> {
            beginParsing()
            val name = parseIdentifier()
            if (lookahead.type === PAR_L) {
                advance()
                val arguments = parseSepList(COMMA, PAR_R, ::parseExpression)
                token(SEMIC)
                finishParsing(CallStatement(name, arguments.map { Argument(it).withPosition(it.position) }))
            } else {
                val target = parseArrayAccesses(NamedVariable(name).withPosition(current.range))
                token(ASSIGN)
                val value = parseExpression()
                token(SEMIC)
                finishParsing(AssignStatement(target, value))
            }
        }

        SEMIC -> {
            beginParsing()
            advance()
            finishParsing(EmptyStatement())
        }

        else -> error(current.range, "statement")
    }

    private fun parseVariable(): Variable {
        beginParsing()
        val name = parseIdentifier()
        val base = finishParsing(NamedVariable(name))
        return parseArrayAccesses(base)
    }

    private fun parseArrayAccesses(base: Variable): Variable {
        var result: Variable = base
        while (lookahead.type === BRA_L) {
            advance()
            val index = parseExpression()
            token(BRA_R)

            result = ArrayAccess(result, index)
                .withPosition(base.position.union(current.range))
        }
        return result
    }


    private fun parseExpression(): Expression = parseComparison()

    private fun parseComparison(): Expression = parseExpressionHierarchy(
        ::BinaryExpression,
        ::parseAddition,
        OP_EQ to BinaryExpression.Operator.EQU,
        OP_NE to BinaryExpression.Operator.NEQ,
        OP_LT to BinaryExpression.Operator.LST,
        OP_LE to BinaryExpression.Operator.LSE,
        OP_GT to BinaryExpression.Operator.GRT,
        OP_GE to BinaryExpression.Operator.GRE,
    )

    private fun parseAddition(): Expression = parseExpressionHierarchy(
        ::BinaryExpression,
        ::parseMultiplication,
        OP_ADD to BinaryExpression.Operator.ADD,
        OP_SUB to BinaryExpression.Operator.SUB,
    )

    private fun parseMultiplication(): Expression = parseExpressionHierarchy(
        ::BinaryExpression,
        ::parseUnaryExpression,
        OP_MUL to BinaryExpression.Operator.MUL,
        OP_DIV to BinaryExpression.Operator.DIV,
    )

    private fun parseUnaryExpression(): Expression = when (lookahead.type) {
        OP_SUB -> {
            beginParsing()
            advance()
            val operand = parseAtomicExpression()
            finishParsing(UnaryExpression(UnaryExpression.Operator.MINUS, operand))
        }

        else -> parseAtomicExpression()
    }

    private fun parseAtomicExpression(): Expression = when (lookahead.type) {
        INTEGER -> {
            beginParsing()
            val value = tokenAs<Int>(INTEGER)
            finishParsing(IntLiteral(value))
        }

        IDENTIFIER -> {
            val variable = parseVariable()
            VariableExpression(variable).withPosition(variable.position)
        }

        PAR_L -> {
            advance()
            val result = parseExpression()
            token(PAR_R)
            result
        }

        else ->
            error(current.range, "expression")
    }
}
