package kotlinx.fuzzer.fuzzing.inputhandlers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MinimizationKtTest {
    @Test
    fun testMinimization() {
        val input = "abjhdbajbdjhabjdhbajhbdfjbafjbajdfba".toByteArray()
        val minimized = minimizeArray(input) { candidate ->
            String(candidate).matches(Regex(".*a.*a.*a.*a.*a.*"))
        }
        assertEquals("aaaaa", String(minimized))
    }

    @Test
    fun testMinimizationEmpty() {
        val input = "abjhdbajbdjhabjdhbajhbdfjbafjbajdfba".toByteArray()
        val minimized = minimizeArray(input) { true }
        assertTrue(minimized.isEmpty())
    }
}
