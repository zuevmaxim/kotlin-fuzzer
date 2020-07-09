package kotlinx.fuzzer.testclasses.coverage

fun singleCode() = 42

fun ifCode(x: Int): Int {
    return if (x > 0) {
        1
    } else {
        2
    }
}

fun forCode(n: Int): Int {
    var res = 0
    for (i in 1..n) {
        res += i
    }
    return res
}

fun tryCatchCode(n: Int): Int {
    return try {
        if (n == 0) {
            throw Exception()
        }
        1
    } catch (e: Throwable) {
        2
    }
}

fun abcd(bytes: ByteArray): Int {
    val s = String(bytes)
    if (s.isNotEmpty() && s[0] == 'a') {
        if (s.length > 1 && s[1] == 'b') {
            if (s.length > 2 && s[2] == 'c') {
                if (s.length > 3 && s[3] == 'd') {
                    error("Crash")
                }
            }
        }
    }
    return 1
}
