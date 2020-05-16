package ru.example.kotlinfuzzer.fuzzing.mutation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ReplaceTextNumberMutationTest {
    private val mutation = ReplaceTextNumberMutation()

    @Test
    fun noNumberTest() {
        val bytes = "Text without numbers".toByteArray()
        assertSame(bytes, mutation.mutate(bytes))
    }

    @Test
    fun mutateTest() {
        val bytes = "Text with number. 56757".toByteArray()
        val mutated = mutation.mutate(bytes)
        assertNotSame(bytes, mutated)
        val mutatedNumber = String(mutated).substring(18).toInt()
        assertTrue(-MAX_REPLACE_NUMBER_VALUE <= mutatedNumber && mutatedNumber <= MAX_REPLACE_NUMBER_VALUE)
    }
}
