package parser.util

open class ExprASTNode {
    open val type: String get() = type
}

open class ValueExprNode<out T> : ExprASTNode() {
    override val type: String get() = type
    open val value: T get() = value
}

class LambdaExprNode(val vars: MutableList<ExprASTNode>, val body: ExprASTNode, override val type: String = "lambda") : ExprASTNode()
class ProgExprNode(val prog: MutableList<ExprASTNode>, override val type: String = "prog") : ExprASTNode()
class IfExprNode(val cond: ExprASTNode, val then: ExprASTNode, val unless: ExprASTNode?, override val type: String = "if") : ExprASTNode()
class CallExprNode(val func: ExprASTNode, val args: MutableList<ExprASTNode>, override val type: String = "call") : ExprASTNode()
class BinaryExprNode(val oprator: String, val left: ExprASTNode, val right: ExprASTNode, override val type: String) : ExprASTNode()

class BoolExprNode(override val value: Boolean, override val type: String = "bool") : ValueExprNode<Boolean>()
class NumberExprNode(override val value: Double, override val type: String = "num") : ValueExprNode<Double>()
class StringExprNode(override val value: String, override val type: String) : ValueExprNode<String>()

class NullExprNode(override val value: Nothing? = null, override val type: String = "null") : ValueExprNode<Nothing?>()
