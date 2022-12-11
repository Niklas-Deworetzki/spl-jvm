package de.thm.mni.compilerbau.phases._01_scanner

import de.thm.mni.compilerbau.CommandLineOptions
import de.thm.mni.compilerbau.position.Range
import java.io.*

class Scanner(private val file: File, options: CommandLineOptions) : Closeable {
    private val input: InputStream = FileInputStream(file)

    private var currentOffset: Long = 0
    private var currentTokenStartOffset: Long = 0

    override fun close() = input.close()

    private fun currentRange() = Range(file, currentTokenStartOffset, currentOffset)

    private fun createToken(type: TokenType, value: Any? = null): Token =
        Token(currentRange(), type, value)


    private val inputBuffer: MutableList<Int> = ArrayDeque()

    private fun getchar(): Int {
        val result = if (inputBuffer.isEmpty()) input.read() else inputBuffer.removeLast()
        if (result != -1) { // Don't increment past end of file.
            currentOffset++
        }
        return result
    }

    private fun ungetchar(character: Int) {
        if (character != -1) {
            currentOffset--
        }
        inputBuffer.add(character)
    }


    private fun skipWhitespace(): Boolean {
        val startOffset = currentOffset
        var current: Int
        do {
            current = getchar()
        } while (current != -1 && Character.isWhitespace(current))
        ungetchar(current)
        return startOffset != currentOffset
    }

    private fun skipComment(): Boolean {
        val firstChar = getchar()
        if (firstChar != '/'.code) {
            ungetchar(firstChar)
            return false
        }
        val secondChar = getchar()
        if (secondChar != '/'.code) {
            ungetchar(secondChar)
            ungetchar(firstChar)
            return false
        }
        var current: Int
        do {
            current = getchar()
        } while (current != -1 && current != '\n'.code && current != '\r'.code)
        ungetchar(current)
        return true
    }

    private fun skipIgnored() {
        while (skipWhitespace() or skipComment());
    }


    fun nextToken(): Token {
        skipIgnored()
        currentTokenStartOffset = currentOffset

        val current = getchar()
        if (current == -1) {
            return createToken(TokenType.EOF)
        } else if (CHARACTER_TOKENS.containsKey(current.toChar())) {
            return createToken(CHARACTER_TOKENS.getValue(current.toChar()))
        } else if (current.toChar().isDigit()) {
            ungetchar(current)
            return consumeInteger()
        } else if (Character.isJavaIdentifierStart(current)) {
            ungetchar(current)
            val identifier = consumeIdentifier()
            val keyword = RESERVED_KEYWORDS[identifier]
            return if (keyword != null) createToken(keyword) else createToken(TokenType.IDENTIFIER, identifier)
        } else when (current.toChar()) {
            '\'' -> return consumeCharacter()
            ':' -> return consumeAssignmentVariant(TokenType.COLON, TokenType.ASSIGN)
            '<' -> return consumeAssignmentVariant(TokenType.OP_LT, TokenType.OP_LE)
            '>' -> return consumeAssignmentVariant(TokenType.OP_GT, TokenType.OP_GE)
            else -> throw LexicalError(currentRange(), String.format("Illegal character 0x%02X in input.", current))
        }
    }


    private fun consumeAssignmentVariant(
        notFollowedByAssignment: TokenType,
        followedByAssignment: TokenType
    ): Token {
        val nextChar = getchar()
        return if (nextChar.toChar() == '=') createToken(followedByAssignment)
        else {
            ungetchar(nextChar)
            createToken(notFollowedByAssignment)
        }
    }

    private val buffer: StringBuilder = StringBuilder()

    private fun consumeIdentifier(): String {
        buffer.setLength(0)

        var currentChar: Int = getchar()
        while (Character.isJavaIdentifierPart(currentChar)) {
            buffer.append(currentChar.toChar())
            currentChar = getchar()
        }

        ungetchar(currentChar)
        return buffer.toString()
    }

    private fun consumeInteger(): Token {
        buffer.setLength(0)

        val firstDigit = getchar()
        val secondDigit = getchar()
        var validDigits = DECIMAL_DIGITS

        var digit: Int
        if (firstDigit.toChar() == '0' && (secondDigit.toChar() == 'x' || secondDigit.toChar() == 'X')) {
            validDigits = HEXADECIMAL_DIGITS
            digit = getchar()
        } else {
            buffer.append(firstDigit.toChar())
            digit = secondDigit
        }

        while (validDigits.contains(digit)) {
            buffer.append(digit.toChar())
            digit = getchar()
        }
        ungetchar(digit)

        val base = if (validDigits == DECIMAL_DIGITS) 10 else 16
        val numericValue = Integer.parseInt(buffer.toString(), base)
        return createToken(TokenType.INTEGER, numericValue)
    }

    private fun consumeCharacter(): Token {
        val firstChar = getchar()
        var secondChar = getchar()
        val result: Char

        if (firstChar == -1 || secondChar == -2) {
            throw LexicalError(currentRange(), "Unclosed character literal.")
        } else if (firstChar.toChar() == '\\') {
            result = ESCAPED_CHARACTERS[secondChar.toChar()]
                ?: throw LexicalError(currentRange(), "Illegal escape character.")
            secondChar = getchar()
        } else {
            result = firstChar.toChar()
        }

        if (secondChar != '\''.code) {
            throw LexicalError(currentRange(), "Unclosed character literal.")
        }

        return createToken(TokenType.INTEGER, result.code)
    }


    companion object {
        private val CHARACTER_TOKENS: Map<Char, TokenType> = mapOf(
            '(' to TokenType.PAR_L,
            ')' to TokenType.PAR_R,
            '[' to TokenType.BRA_L,
            ']' to TokenType.BRA_R,
            '{' to TokenType.CUR_L,
            '}' to TokenType.CUR_R,
            '=' to TokenType.OP_EQ,
            '#' to TokenType.OP_NE,
            '+' to TokenType.OP_ADD,
            '-' to TokenType.OP_SUB,
            '*' to TokenType.OP_MUL,
            '/' to TokenType.OP_DIV,
            ',' to TokenType.COMMA,
            ';' to TokenType.SEMIC,
        )

        private val RESERVED_KEYWORDS: Map<String, TokenType> = mapOf(
            "type" to TokenType.TYPE,
            "proc" to TokenType.PROC,
            "array" to TokenType.ARRAY,
            "of" to TokenType.OF,
            "ref" to TokenType.REF,
            "var" to TokenType.VAR,
            "if" to TokenType.IF,
            "else" to TokenType.ELSE,
            "while" to TokenType.WHILE,
            "do" to TokenType.DO,
        )

        private val DECIMAL_DIGITS: Set<Int> = "0123456789".map(Char::code).toSet()
        private val HEXADECIMAL_DIGITS: Set<Int> = "ABCDEFabcdef0123456789".map(Char::code).toSet()

        private val ESCAPED_CHARACTERS: Map<Char, Char> = mapOf(
            'n' to '\n',
            'r' to '\r',
            '\\' to '\\',
        )

        class LexicalError(val range: Range, message: String) : Exception(message)
    }
}