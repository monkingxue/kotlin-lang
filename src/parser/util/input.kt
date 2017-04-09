package parser.util

class InputSteam(private val input: String) {
    private var pos: Int = 0
    private var line: Int = 1
    private var col: Int = 0

    fun next(): String {
        val ch: Char = input[pos];pos++
        if (ch == '\n') {
            line++;col = 0
        }
        else col++
        return ch.toString()
    }

    fun peek(): String {
        try {
            return input.elementAt(pos).toString()
        } catch (e: IndexOutOfBoundsException) {
            return ""
        }
    }

    fun eof(): Boolean = peek() == ""

    fun croak(msg: String): Unit {
        throw Error("$msg in ($line : $col)")
    }
}