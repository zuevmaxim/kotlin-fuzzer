package kotlinx.fuzzer.fuzzing.annotationtest

import kotlinx.fuzzer.Fuzz
import kotlinx.fuzzer.Fuzzer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File


internal class ThrowStrategyTest {
    @Fuzz("testD")
    fun fuzz(bytes: ByteArray): Int {
        check(bytes.size < 5)
        return 1
    }

    @Test
    fun testThrowsFuzzMethodException() {
        assertThrows<IllegalStateException> {
            Fuzzer<ThrowStrategyTest>().start()
        }
        assertTrue(File("testD").deleteRecursively())
    }
}
