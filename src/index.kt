import parser.*
import parser.util.*


fun main(args: Array<String>) {
    val code: String = "1+2"
    val input = InputSteam(code)
    val token = TokenStream(input)
    val dest = Parser(token)
}