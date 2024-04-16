package kotlinx.fuzzer.tests.io

import kotlinx.io.*

class Utf8 {
    fun fuzz(bytes: ByteArray): Int {
        val ba = Buffer().also { it.write(bytes) }
        val s1 = kotlin.runCatching { ba.readString() }
        val s2 = kotlin.runCatching { java.lang.String(bytes) as String }
        if (s1 != s2) {
            error("Mismatch! ")
        }
        return 1
    }
}