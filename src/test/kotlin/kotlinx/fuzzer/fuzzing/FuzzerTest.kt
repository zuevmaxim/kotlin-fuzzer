package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.Fuzz
import kotlinx.fuzzer.Fuzzer
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test


internal class FuzzerTest {
    @Fuzz("test")
    fun fuzz(bytes: ByteArray): Int {
        check(bytes.size < 5)
        return 1
    }

    @Test
    @Disabled("Runs infinitely. Should be run manually.")
    fun annotationTest() {
        Fuzzer(FuzzerTest::class.java).start()
    }
}
