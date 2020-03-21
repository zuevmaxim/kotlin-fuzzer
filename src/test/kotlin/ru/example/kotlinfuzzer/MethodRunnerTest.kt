package ru.example.kotlinfuzzer

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class MethodRunnerTest {
    companion object {
        val doneMethods = hashSetOf<String>()
        private const val CLASS_LOCATION = "build/classes/kotlin/test/ru/example/kotlinfuzzer/testclasses/"
        private const val CLASS_NAME = "ru.example.kotlinfuzzer.testclasses.TestClass"
        private lateinit var methodRunner: MethodRunner

        @JvmStatic
        @BeforeAll
        fun init() {
            methodRunner = MethodRunner(CLASS_LOCATION, CLASS_NAME)
        }

        @JvmStatic
        @AfterAll
        fun finish() {
            methodRunner.shutdown()
        }

    }

    @ParameterizedTest
    @ValueSource(strings = ["testNoArg", "testOneArg", "testTwoArgs", "testStringArgs"])
    fun runMethodTest(methodName: String) {
        assertFalse(doneMethods.contains(methodName))
        methodRunner.run(methodName)
        assertTrue(doneMethods.contains(methodName))
    }

    @Test
    fun simpleCoverageTest() {
        val result = methodRunner.run("simpleCoverageTest")
        assertEquals(2, result.totalMethods - result.missedMethods)
        assertEquals(4, result.totalLines - result.missedLines)
        assertEquals(0, result.totalBranches - result.missedBranches)
    }

    @Test
    fun coverageTest() {
        val result = methodRunner.run("coverageTest")
        assertEquals(2, result.totalMethods - result.missedMethods)
        assertTrue(2 <= result.totalBranches - result.missedBranches)
        assertTrue(5 >= result.totalBranches - result.missedBranches)
    }

}
