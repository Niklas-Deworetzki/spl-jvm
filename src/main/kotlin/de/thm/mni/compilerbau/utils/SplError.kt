package de.thm.mni.compilerbau.utils

import de.thm.mni.compilerbau.absyn.BinaryExpression
import de.thm.mni.compilerbau.absyn.Position
import de.thm.mni.compilerbau.absyn.UnaryExpression
import de.thm.mni.compilerbau.table.Identifier
import de.thm.mni.compilerbau.types.Type

/**
 * An exception class, that encapsulates all possible SPL errors.
 * Contains static methods that construct exceptions for specific errors.
 */
class SplError private constructor(val errorCode: Int, val position: Position, message: String) : RuntimeException(message) {

    companion object {
        @JvmStatic
        fun SyntaxError(position: Position, token: String): SplError {
            return SplError(100, position, "syntax error. Unexpected token '$token'")
        }

        fun UndefinedType(position: Position, name: Identifier): SplError {
            return SplError(101, position, "undefined type $name")
        }

        fun NotAType(position: Position, name: Identifier): SplError {
            return SplError(102, position, "$name is not a type")
        }

        fun RedeclarationAsType(position: Position, name: Identifier): SplError {
            return SplError(103, position, "redeclaration of $name as type")
        }

        fun MustBeAReferenceParameter(position: Position, name: Identifier): SplError {
            return SplError(104, position, "parameter $name must be a reference parameter")
        }

        fun RedeclarationAsProcedure(position: Position, name: Identifier): SplError {
            return SplError(105, position, "redeclaration of $name as procedure")
        }

        fun RedeclarationAsParameter(position: Position, name: Identifier): SplError {
            return SplError(106, position, "redeclaration of $name as parameter")
        }

        fun RedeclarationAsVariable(position: Position, name: Identifier): SplError {
            return SplError(107, position, "redeclaration of $name as variable")
        }

        fun IllegalAssignment(position: Position, left: Type, right: Type): SplError {
            return SplError(108, position, "illegal assignment '<$left> := <$right>'")
        }

        fun IllegalAssignmentToArray(position: Position): SplError {
            return SplError(109, position, "illegal assignment to array.")
        }

        fun IfConditionMustBeBoolean(position: Position, actual: Type): SplError {
            return SplError(110, position, "'if' test expression must be of type boolean: actual = $actual")
        }

        fun WhileConditionMustBeBoolean(position: Position, actual: Type): SplError {
            return SplError(111, position, "'while' test expression must be of type boolean: actual = $actual")
        }

        fun UndefinedProcedure(position: Position, name: Identifier): SplError {
            return SplError(112, position, "undefined procedure $name")
        }

        fun CallOfNonProcedure(position: Position, name: Identifier): SplError {
            return SplError(113, position, "call of non-procedure $name")
        }

        fun ArgumentTypeMismatch(position: Position, name: Identifier, argumentIndex: Int, expected: Type, actual: Type): SplError {
            return SplError(114, position, "procedure $name argument $argumentIndex type mismatch: expected = $expected, actual = $actual")
        }

        fun ArgumentMustBeAVariable(position: Position, name: Identifier, argumentIndex: Int): SplError {
            return SplError(115, position, "procedure $name argument $argumentIndex must be a variable")
        }

        fun TooFewArguments(position: Position, name: Identifier): SplError {
            return SplError(116, position, "procedure $name called with too few arguments")
        }

        fun TooManyArguments(position: Position, name: Identifier): SplError {
            return SplError(117, position, "procedure $name called with too many arguments")
        }

        fun NoSuchOperator(position: Position, operator: BinaryExpression.Operator, leftType: Type, rightType: Type): SplError {
            return SplError(118, position, "There is no binary operator '<${leftType}> ${operator.operatorString()} <${rightType}>'.")
        }

        fun NoSuchOperator(position: Position, operator: UnaryExpression.Operator, rightType: Type): SplError {
            return SplError(119, position, "There is no unary operator '${operator.operatorString()} <${rightType}>'.")
        }

        fun UndefinedVariable(position: Position, name: Identifier): SplError {
            return SplError(121, position, "undefined variable $name")
        }

        fun NotAVariable(position: Position, name: Identifier): SplError {
            return SplError(122, position, "$name is not a variable")
        }

        fun IndexingNonArray(position: Position): SplError {
            return SplError(123, position, "illegal indexing a non-array")
        }

        fun IndexingWithNonInteger(position: Position): SplError {
            return SplError(124, position, "illegal indexing with a non-integer")
        }

        fun MainIsMissing(): SplError {
            return SplError(125, Position.ERROR_POSITION, "procedure 'main' is missing")
        }

        fun MainIsNotAProcedure(): SplError {
            return SplError(126, Position.ERROR_POSITION, "'main' is not a procedure")
        }

        fun MainMustNotHaveParameters(): SplError {
            return SplError(127, Position.ERROR_POSITION, "procedure 'main' must not have any parameters")
        }

        @JvmStatic
        fun IllegalApostrophe(position: Position): SplError {
            return SplError(99, position, "illegal use of apostrophe")
        }

        @JvmStatic
        fun IllegalCharacter(position: Position, character: Char): SplError {
            return SplError(99, position, "Illegal character ${if (Character.isISOControl(character)) "0x" + character.code.toString(16) else "'$character'"}")
        }

        fun RegisterOverflow(): SplError {
            return SplError(140, Position.ERROR_POSITION, "There are not enough registers to run this program!")
        }
    }
}