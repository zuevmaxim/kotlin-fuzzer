package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.Fuzz
import kotlinx.fuzzer.FuzzCrash
import kotlinx.fuzzer.Fuzzer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File


internal class FuzzerAnnotationTest {
    @Fuzz("testA")
    fun fuzz(bytes: ByteArray): Int {
        check(bytes.size < 5)
        return 1
    }

    @FuzzCrash
    fun callback(e: Throwable, data: ByteArray) {
        assert(false)
    }

    @Test
    fun annotationTest() {
        assertThrows<AssertionError> {
            Fuzzer(FuzzerAnnotationTest::class.java).start()
        }
        assertTrue(File("testA").deleteRecursively())
    }
}

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
        Fuzzer(FuzzerTimeoutTest::class.java).start(2)
        assertTrue(File("testB").deleteRecursively())
    }
}
