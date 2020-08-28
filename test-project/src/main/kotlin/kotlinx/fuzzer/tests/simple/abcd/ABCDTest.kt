package kotlinx.fuzzer.tests.simple.abcd

class ABCDTest {
    fun test(bytes: ByteArray): Int {
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
}
