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

fun switchCode(n: Int) = when (n) {
    0 -> 23
    1 -> 35
    3 -> 46
    4 -> 15
    else -> 42
}

fun lookupSwitchCode(n: Int) = when (n) {
    0 -> 23
    10 -> 35
    100 -> 46
    1000 -> 15
    else -> 42
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

fun recursion(n: Int): Int {
    if (n == 0) return 1
    return recursion(n - 1) * n
}

fun multiThread(x: Int, y: Int): Int {
    var result = -1
    val thread = Thread {
        result = if (x == 0) {
            if (y > 0) {
                1
            } else {
                2
            }
        } else {
            if (y >= 0) {
                3
            } else {
                4
            }
        }
    }
    thread.start()
    thread.join()
    return result
}
