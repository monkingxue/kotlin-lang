package parser.test

val str = "(:)"

val hasToken: (String) -> (String) -> Boolean
        = { content: String -> { token: String -> content.toRegex().matches(token) } }

val isString = hasToken(str)

fun main(args: Array<String>) {

    print(isString("\""))
}
