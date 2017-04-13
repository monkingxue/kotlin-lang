package parser.test

import parser.util.ValueExprNode

class ExprASTNode(val type: String)

val hasToken: (String) -> (String) -> Boolean
        = { content: String -> { token: String -> content.toRegex().matches(token) } }

fun main(args: Array<String>) {

    val list = mutableListOf<ExprASTNode>()
    val text = "if"
    list.add(ExprASTNode("1"))
    val add = { ch: Int -> ch + 1 }
//    print(list[0])
}
