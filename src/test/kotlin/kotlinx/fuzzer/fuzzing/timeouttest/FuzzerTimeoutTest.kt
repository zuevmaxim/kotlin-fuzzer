package kotlinx.fuzzer.fuzzing.timeouttest

import kotlinx.fuzzer.Fuzz
import kotlinx.fuzzer.FuzzCrash
import kotlinx.fuzzer.Fuzzer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

internal class FuzzerTimeoutTest {
    @Fuzz("testB")
    fun fuzz(bytes: ByteArray): Int {
        return 1
    }

    @FuzzCrash
    fun callback(e: Throwable, data: ByteArray) {
        throw e
    }

    @Test
    fun timeoutTest() {
        Fuzzer<FuzzerTimeoutTest>().start(2)
        Assertions.assertTrue(File("testB").deleteRecursively())
    }
}
