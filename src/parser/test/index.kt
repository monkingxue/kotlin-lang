package parser.test

class ExprASTNode(val type: String)


val str = "(:)"

val hasToken: (String) -> (String) -> Boolean
        = { content: String -> { token: String -> content.toRegex().matches(token) } }

val isString = hasToken(str)

fun main(args: Array<String>) {

    val list = mutableListOf<ExprASTNode>()
    list.add(ExprASTNode("1"))
    print(list[0])
}
