package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.Fuzz
import kotlinx.fuzzer.FuzzCrash
import kotlinx.fuzzer.Fuzzer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class FuzzerTest {
    @Fuzz("test")
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
            Fuzzer(FuzzerTest::class.java).start()
        }
    }
}
