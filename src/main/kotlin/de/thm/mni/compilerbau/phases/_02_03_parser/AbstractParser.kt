package de.thm.mni.compilerbau.phases._02_03_parser

import de.thm.mni.compilerbau.absyn.Node
import de.thm.mni.compilerbau.absyn.Node.Companion.withPosition
import de.thm.mni.compilerbau.phases._01_scanner.Scanner
import de.thm.mni.compilerbau.phases._01_scanner.Token
import de.thm.mni.compilerbau.phases._01_scanner.TokenType
import de.thm.mni.compilerbau.position.Range
import java.util.*
import kotlin.collections.ArrayDeque

abstract class AbstractParser<P>(private val scanner: Scanner) {
    val reportedErrors: MutableList<SyntaxErrorReport> = ArrayList<SyntaxErrorReport>()

    protected var lookahead: Token = scanner.nextToken()
    protected lateinit var current: Token

    abstract fun describeTokenType(tokenType: TokenType): String

    abstract fun parserEntry(): P

    fun parse(): P? {
        val result = try {
            parseFully(::parserEntry)
        } catch (syntaxError: SyntaxError) {
            null
        }
        if (reportedErrors.isNotEmpty()) return null
        return result
    }

    protected fun advance() {
        current = lookahead
        lookahead = scanner.nextToken()
    }


    private val positionStack: MutableList<Range> = ArrayDeque()

    protected fun beginParsing() {
        positionStack.add(lookahead.range)
    }

    protected fun popPosition(): Range =
        positionStack.removeLast()

    protected fun <P : Node> finishParsing(result: P): P {
        return result.withPosition(popPosition().union(current.range))
    }


    protected fun <T> parseFully(parser: Parser<T>): T {
        val result = parser.parse()
        if (lookahead.type !== TokenType.EOF) {
            unrecoverableError(current.range, "end of file")
        }
        return result
    }

    protected fun reentryAt(tokenTypes: Set<TokenType>) {
        while (!tokenTypes.contains(lookahead.type)) {
            if (lookahead.type === TokenType.EOF) throw SyntaxError()
            advance()
        }
    }

    protected fun isPresent(tokenType: TokenType): Boolean =
        if (lookahead.type === tokenType) {
            advance()
            true
        } else false

    protected fun token(tokenType: TokenType, expected: String? = null): Unit =
        if (lookahead.type === tokenType) advance()
        else {
            popPosition()
            error(lookahead.range, expected ?: tokenType)
        }

    protected inline fun <reified T> tokenAs(tokenType: TokenType, expected: String? = null): T =
        if (lookahead.type === tokenType) {
            advance()
            current.value as T
        } else {
            popPosition()
            error(lookahead.range, expected ?: tokenType)
        }

    fun <T> parseRepeated(parser: Parser<T>, firstSet: Set<TokenType>, followSet: Set<TokenType>): List<T> {
        val results: MutableList<T> = mutableListOf()

        while (lookahead.type !in followSet) {
            try {
                results.add(parser.parse())
            } catch (recovery: Recovery) {
                reentryAt(firstSet.union(followSet))
            }
        }

        return results
    }

    protected fun <T> parseSepList(separator: TokenType, terminator: TokenType, parser: Parser<T>): List<T> {
        if (lookahead.type === terminator) {
            advance()
            return emptyList()
        }

        val results: MutableList<T> = ArrayList()
        do {
            if (results.isNotEmpty()) advance() // Skip separator.

            try {
                results.add(parser.parse())
            } catch (recovery: Recovery) {
                if (lookahead.type === separator || lookahead.type === terminator) {
                    advance() // Prevent being stuck on a separator without consuming input.
                }
                reentryAt(setOf(separator, terminator))
            }
        } while (lookahead.type === separator)

        if (lookahead.type !== terminator) {
            reportError(lookahead.range, listOf(describeTokenType(separator), describeTokenType(terminator)))
            reentryAt(setOf(terminator))
        }
        advance()
        return results
    }

    protected fun <N : Node, O> parseExpressionHierarchy(
        constructor: (N, O, N) -> N,
        lowerLevel: Parser<N>,
        vararg operators: Pair<TokenType, O>
    ): N {
        val operatorMap = operators.toMap()
        var result = lowerLevel.parse()
        val beginPosition = result.position

        while (lookahead.type in operatorMap.keys) {
            val operator = operatorMap[lookahead.type]!!
            advance()
            val rhs = lowerLevel.parse()
            result = constructor(result, operator, rhs)
                .withPosition(beginPosition.union(rhs.position))
        }
        return result
    }

    /**
     * An interface modelling a parser for combinator methods.
     * <p>
     * Parsers have a single method that returns some parsed
     * element while consuming input as a side effect. A parser
     * may report a {@link SyntaxError} during parsing.
     *
     * @param <R> Type of elements returned by the parser.
     */
    fun interface Parser<R> {
        fun parse(): R
    }


    /**
     * Creates a new {@link SyntaxErrorReport} and returns <code>true</code> when the
     * error limit is exceeded. Otherwise, <code>false</code> is returned.
     */
    private fun reportError(range: Range, expected: List<String>) {
        reportedErrors.add(SyntaxErrorReport(range, expected))
    }

    protected fun error(range: Range, vararg expected: Any): Nothing {
        reportError(range, formatExpected(expected))
        throw Recovery()
    }

    private fun unrecoverableError(range: Range, vararg expected: Any): Nothing {
        reportError(range, formatExpected(expected))
        throw SyntaxError()
    }

    private fun formatExpected(expected: Array<out Any>): List<String> =
        expected.map {
            if (it is TokenType) describeTokenType(it)
            else Objects.toString(it)
        }


    /**
     * Special {@link Exception} class indicating that error recovery can be attempted.
     */
    class Recovery : SyntaxError()

    /**
     * Special {@link Exception} class indicating that parsing should be aborted.
     */
    open class SyntaxError : Exception()
}