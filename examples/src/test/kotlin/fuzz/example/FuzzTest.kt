package fuzz.example

import kotlinx.fuzzer.Fuzz
import kotlinx.fuzzer.Fuzzer
import org.junit.jupiter.api.Test

class FuzzTest {

    @Fuzz(workingDirectory = "results")
    fun fuzz(bytes: ByteArray): Int {
        if (bytes.size < 4) { // uninteresting input
            return 0
        }
        val string = String(bytes)
        failOnABCD(string) // may crash
        return 1 // passed OK
    }

    @Test
    fun test() {
        Fuzzer(FuzzTest::class.java).start()
    }
}
