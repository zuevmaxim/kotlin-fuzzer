package kotlinx.fuzzer.fuzzing.input

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

internal class ExceptionWrapperTest {
    private fun a() {
        throw Exception("a")
    }

    private fun b() {
        throw Exception("b")
    }

    private fun c(message: String) {
        throw Exception(message)
    }

    @Test
    fun testEquals() {
        val exceptions = mutableListOf<ExceptionWrapper>()
        repeat(2) {
            try {
                if (it == 0) c("first") else c("second")
            } catch (e: Exception) {
                exceptions += ExceptionWrapper(e)
            }
        }
        val (e1, e2) = exceptions
        assertEquals(e1.hashCode(), e2.hashCode())
        assertEquals(e1, e2)
    }

    @Test
    fun testNotEquals() {
        val exceptions = mutableListOf<ExceptionWrapper>()
        repeat(2) {
            try {
                if (it == 0) a() else b()
            } catch (e: Exception) {
                exceptions += ExceptionWrapper(e)
            }
        }
        val (e1, e2) = exceptions
        assertNotEquals(e1, e2)
    }

    @Test
    fun testReflection() {
        val e1 = try {
            ExceptionWrapperTest::class.java.getDeclaredMethod("c", String::class.java).invoke(this, "a")
            Exception()
        } catch (e: Exception) {
            e.cause!!
        }.let { ExceptionWrapper(it) }

        val e2 = try {
            ExceptionWrapperTest::class.java.getDeclaredMethod("c", String::class.java).invoke(this, "b")
            Exception()
        } catch (e: Exception) {
            e.cause!!
        }.let { ExceptionWrapper(it) }
        assertEquals(e1.hashCode(), e2.hashCode())
        assertEquals(e1, e2)
    }
}
