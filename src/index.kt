import parser.*
import parser.util.*


fun main(args: Array<String>) {
    val code: String = "if foo then bar"
    val input = InputSteam(code)
    val token = TokenStream(input)
    val dest = Parser(token)
    println(dest)
}