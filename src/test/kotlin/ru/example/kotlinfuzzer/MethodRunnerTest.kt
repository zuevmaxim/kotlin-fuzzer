package ru.example.kotlinfuzzer

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MethodRunnerTest {
    companion object {
        val doneMethods = hashSetOf<String>()
        private const val CLASS_LOCATION = "build/classes/kotlin/test/ru/example/kotlinfuzzer"
        private const val CLASS_NAME = "ru.example.kotlinfuzzer.TestClass"
    }

    @Test
    fun test() {
        val methodRunner = MethodRunner(CLASS_LOCATION, CLASS_NAME)
        listOf("testNoArg", "testOneArg", "testTwoArgs", "testStringArgs").forEach {
            assertFalse(doneMethods.contains(it))
            methodRunner.run(it)
            assertTrue(doneMethods.contains(it))
        }
    }
}
