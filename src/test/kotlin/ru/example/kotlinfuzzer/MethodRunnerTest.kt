package ru.example.kotlinfuzzer

import org.junit.jupiter.api.Assertions.*
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

    @Test
    fun simpleCoverageTest() {
        val methodRunner = MethodRunner(CLASS_LOCATION, CLASS_NAME)
        val result = methodRunner.run("simpleCoverageTest")
        assertEquals(2, result.methodCounter.totalCount - result.methodCounter.missedCount)
        assertEquals(4, result.lineCounter.totalCount - result.lineCounter.missedCount)
        assertEquals(0, result.branchCounter.totalCount - result.branchCounter.missedCount)
    }

    @Test
    fun coverageTest() {
        val methodRunner = MethodRunner(CLASS_LOCATION, CLASS_NAME)
        val result = methodRunner.run("coverageTest")
        assertEquals(2, result.methodCounter.totalCount - result.methodCounter.missedCount)
        assertTrue(2 <= result.branchCounter.totalCount - result.branchCounter.missedCount)
        assertTrue(5 >= result.branchCounter.totalCount - result.branchCounter.missedCount)
    }

}
