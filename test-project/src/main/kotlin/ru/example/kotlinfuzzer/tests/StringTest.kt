package ru.example.kotlinfuzzer.tests

class StringTest {

    fun a(bytes: ByteArray): Int {
        val s = String(bytes)
        if (s.isNotEmpty() && s[0] == 'a') {
            if (s.length > 1 && s[1] == 'b') {
                if (s.length > 2 && s[2] == 'c') {
                    if (s.length > 3 && s[3] == 'd') {
                        println(s[s[4].toInt()])
                    }
                }
            }
        }
        return 1
    }

    fun b(bytes: ByteArray): Int {
        check(bytes.size < 20)
        return 1
    }
}
