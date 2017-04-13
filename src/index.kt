import parser.*
import parser.util.*


fun main(args: Array<String>) {
    val code: String = "sum = lambda (x) x+1;"
    val input = InputSteam(code)
    val token = TokenStream(input)
    val dest = Parser(token)
    println(dest)
}