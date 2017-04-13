package parser

import parser.util.*

val PRECEDENCE: Map<String, Int> = mapOf(
        "=" to 1,
        "||" to 2,
        "&&" to 3,
        "<" to 6, ">" to 6, "<=" to 6, ">=" to 6, "==" to 6, "!=" to 6,
        "+" to 10, "-" to 10,
        "*" to 20, "/" to 20, "%" to 20
)

class Parser(val input: TokenStream) {
    var ast: ProgExprNode? = null

    private val isToken = { type: String ->
        { value: String ->
            val token = input.peek()
            if (token.type != "null")
                token is ValueExprNode<*> && token.type == type
                        && (!value.toBoolean() || token.value == value)
            else false
        }
    }

    private val isPunc = isToken("punc")

    private val isKeyword = isToken("kw")

    private val isOp = isToken("op")

    private val skipToken = { predicate: (String) -> Boolean ->
        {
            type: String ->
            if (predicate(type)) input.next() else input.croak("Expecting \"$type\"")
        }
    }

    private val skipPunc = skipToken(isPunc)

    private val skipKeyword = skipToken(isKeyword)

    private val skipOp = skipToken(isOp)

    init {
        ast = parseTopLevel()
    }

    private fun unexpected(): Unit = input.croak("Unexpected token: ${input.peek()}")

    private fun parseLambda(): LambdaExprNode = LambdaExprNode(delimited("(", ")", ",") { parseVarName() }, parseExpression())

    private fun parseTopLevel(): ProgExprNode {
        var prog = mutableListOf<ExprASTNode>()
        while (!input.eof()) {
            prog.add(parseExpression())
            if (!input.eof()) skipPunc(";")
        }
        return ProgExprNode(prog)
    }

    private fun parseIf(): IfExprNode {
        skipKeyword("if")
        val cond: ExprASTNode = parseExpression()
        if (!isPunc("{")) skipKeyword("then")
        val then: ExprASTNode = parseExpression()
        var unless: ExprASTNode? = null
        if (isKeyword("else")) {
            input.next()
            unless = parseExpression()
        }
        return IfExprNode(cond, then, unless)
    }

    private fun parseVarName(): ExprASTNode {
        val name = input.next()
        if (name.type != "var") {
            input.croak("Expecting variable name")
        }
        return StringExprNode(name.value.toString(), "var")
    }

    private fun parseBool(): ExprASTNode = BoolExprNode(input.next().value == "true")

    private fun delimited(start: String, stop: String, separator: String, parser: () -> ExprASTNode)
            : MutableList<ExprASTNode> {
        var first: Boolean = true
        var tokenList = mutableListOf<ExprASTNode>()
        skipPunc(start)
        while (!input.eof()) {
            if (isPunc(stop)) break
            if (first) first = false else skipPunc(separator)
            if (isPunc(stop)) break
            tokenList.add(parser())
        }
        skipPunc(stop)
        return tokenList
    }

    private fun parseAtom(): ExprASTNode {
        return maybeCall(fun(): ExprASTNode {
            when {
                isPunc("(") -> {
                    input.next()
                    val exp = parseExpression()
                    skipPunc(")")
                    return exp
                }
                isPunc("{") -> return parseProg()
                isKeyword("if") -> return parseIf()
                isKeyword("true") || isKeyword("false") -> return parseBool()
                isKeyword("lambda") -> {
                    input.next()
                    return parseLambda()
                }
            }
            val token = input.next()
            if (token.type != "null" && (token.type == "var"
                    || token.type == "num" || token.type == "str")) {
                return token
            }
            unexpected()
            return NullExprNode()
        })
    }

    private fun parseProg(): ExprASTNode {
        val prog = delimited("{", "}", ";") { parseExpression() }
        if (prog.isEmpty()) return BoolExprNode(false)
        if (prog.size == 1) return prog[0]
        return ProgExprNode(prog)
    }

    private fun parseExpression(): ExprASTNode = maybeCall { maybeBinary(parseAtom(), 0) }

    private fun maybeCall(handleExpr: () -> ExprASTNode): ExprASTNode {
        val expr = handleExpr()
        val parseCall = { expr: ExprASTNode -> CallExprNode(expr, delimited("(", ")", ",") { parseExpression() }) }

        return if (isPunc("(")) parseCall(expr) else expr
    }

    private fun maybeBinary(leftNode: ExprASTNode, LPEC: Int): ExprASTNode {
        val opNode: ValueExprNode<*> = if (isOp("")) input.peek() else NullExprNode()
        if (opNode.type != "null") {
            val RPEC: Int = PRECEDENCE.getOrDefault(opNode.value.toString(), -1)
            if (LPEC < RPEC) {
                input.next()
                val rightNode = maybeBinary(parseAtom(), RPEC)
                val binaryNode = BinaryExprNode(
                        oprator = opNode.value.toString(),
                        left = leftNode,
                        right = rightNode,
                        type = if (opNode.value == "=") "assign" else "binary"
                )
                return maybeBinary(binaryNode, LPEC)
            }
        }
        return leftNode
    }
}