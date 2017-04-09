package parser.util

data class Token<T>(val type: String, val value: T)

/**
 * regex pattern string
 */
val keywords = "(let|if|then|else|lambda|true|false)"
val ops = "(\\+|-|\\*|/|%|=|&|<|>|!)"
val punc = "(,|;|\\(|\\)|\\{|\\}|\\[|\\]|:)"
val whitespace = "( |\n|\t|)"
val number = "[0-9]"
val idstart = "([a-z]|_|λ)"
val id = "([a-z]|[A-Z]|\\?|!|-|<|>|=)"
val comment = "(#)"
val string = "(\")"

/**
 * root function of filter
 */
val hasToken: (String) -> (String) -> Boolean
        = { content: String -> { token: String -> content.toRegex().matches(token) } }

/**
 * token filter
 * (String) -> Boolean
 */
val isKeyword = hasToken(keywords)
val isOp = hasToken(ops)
val isPunc = hasToken(punc)
val isWhitespace = hasToken(whitespace)
val isDigit = hasToken(number)
val isIdStart = hasToken(idstart)
val isId = hasToken(id)
val isComment = hasToken(comment)
val isString = hasToken(string)

class TokenStream(val input: InputSteam) {
    var current: Token<*>? = null

    private inline fun readWhile(predicate: (String) -> Boolean): String {
        var str: String = ""
        while (!input.eof() && predicate(input.peek()))
            str += input.next()
        return str
    }

    private fun readNumber(): Token<Float> {
        var hasDot = false
        val numStr = readWhile(
                fun(ch): Boolean {
                    if (ch === ".")
                        if (hasDot)
                            return false
                        else {
                            hasDot = true
                            return true
                        }
                    return isDigit(ch)
                }
        )
        return Token("num", numStr.toFloat())
    }

    private fun readIdent(): Token<String> {
        val id = readWhile(isId)
        val type = if (isKeyword(id)) "kw" else "var"
        return Token(type, id)
    }

    private fun readEscaped(end: String): String {
        var escaped = false
        var str = ""
        input.next()

        loop@ while (!input.eof()) {
            val ch = input.next()
            when (ch) {
                "\\" -> continue@loop
                end -> break@loop
                else -> {
                    str += ch
                }
            }
        }
        return str
    }

    private fun readString(): Token<String> = Token("str", readEscaped("\"\""))

    private fun readPunc(): Token<String> = Token("punc", input.next())

    private fun readOp(): Token<String> = Token("op", readWhile(isOp))

    private fun skipComment(): Unit {
        readWhile { it != "\n" }
        input.next()
    }

    private fun readNext(): Token<*>? {
        readWhile(isWhitespace)
        if (input.eof()) return null
        val ch = input.peek()
        when {
            isComment(ch) -> {
                skipComment()
                return readNext()
            }
            isString(ch) -> return readString()
            isDigit(ch) -> return readNumber()
            isIdStart(ch) -> return readIdent()
            isPunc(ch) -> return readPunc()
            isOp(ch) -> return readOp()
        }
        input.croak("Can't handle character: $ch")
        return null
    }

    fun peek(): Token<*>? {
        if(current == null)
            current = readNext()
        return current
    }

    fun next(): Token<*>? {
        val token = current
        current = null
        return token ?: readNext()
    }

    fun eof(): Boolean = peek() == null

    fun croak(msg: String): Unit = input.croak(msg)
}
