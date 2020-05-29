package kotlinx.fuzzer.fuzzing.input

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.IOException

internal class MyStackTraceElementTest {

    @Test
    fun testEquals() {
        val exceptions = mutableListOf<Exception>()
        repeat(2) {
            try {
                if (it == 0) throw Exception() else throw IOException()
            } catch (e: Exception) {
                exceptions += e
            }
        }
        val (e1, e2) = exceptions
            .map { it.stackTrace!![0] }
            .map { MyStackTraceElement(it) }
        assertEquals(e1, e2)
        assertEquals(e1.hashCode(), e2.hashCode())
    }
}
