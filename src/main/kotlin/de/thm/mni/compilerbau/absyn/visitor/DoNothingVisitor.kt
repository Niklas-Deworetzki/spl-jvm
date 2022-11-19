package de.thm.mni.compilerbau.absyn.visitor

import de.thm.mni.compilerbau.absyn.*

/**
 * This implementation of a [Visitor] implements every visit-method but does nothing in every case.
 *
 * It can be used to hide unneeded implementations in a visitor.
 * If for example a visitor would only need a visit-method for the [Program]-node, the java code would not compile
 * until every other method is implemented. When the [DoNothingVisitor] is extended in this case, all other methods
 * are implemented in this superclass and thus no compile-errors are produced.
 */
class DoNothingVisitor : Visitor {
    override fun visit(arrayAccess: ArrayAccess) {}
    override fun visit(arrayTypeExpression: ArrayTypeExpression) {}
    override fun visit(assignStatement: AssignStatement) {}
    override fun visit(binaryExpression: BinaryExpression) {}
    override fun visit(unaryExpression: UnaryExpression) {}
    override fun visit(callStatement: CallStatement) {}
    override fun visit(compoundStatement: CompoundStatement) {}
    override fun visit(emptyStatement: EmptyStatement) {}
    override fun visit(ifStatement: IfStatement) {}
    override fun visit(intLiteral: IntLiteral) {}
    override fun visit(namedTypeExpression: NamedTypeExpression) {}
    override fun visit(namedVariable: NamedVariable) {}
    override fun visit(parameterDeclaration: ParameterDeclaration) {}
    override fun visit(procedureDeclaration: ProcedureDeclaration) {}
    override fun visit(program: Program) {}
    override fun visit(typeDeclaration: TypeDeclaration) {}
    override fun visit(variableDeclaration: VariableDeclaration) {}
    override fun visit(variableExpression: VariableExpression) {}
    override fun visit(whileStatement: WhileStatement) {}
}