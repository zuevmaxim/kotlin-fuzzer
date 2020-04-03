package ru.example.kotlinfuzzer

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.example.kotlinfuzzer.classload.Loader

internal class SingleClassMethodRunnerTest {
    companion object {
        val doneMethods = hashSetOf<String>()
        private const val CLASS_LOCATION =
            "build/classes/kotlin/test/ru/example/kotlinfuzzer/testclasses/singleclasstest/"
        private const val PACKAGE_NAME = "ru.example.kotlinfuzzer.testclasses.singleclasstest"
        private const val CLASS_NAME = "ru.example.kotlinfuzzer.testclasses.singleclasstest.TestClass"
        private lateinit var methodRunner: MethodRunner

        @JvmStatic
        @BeforeAll
        fun init() {
            val loader = Loader(listOf(CLASS_LOCATION), listOf(PACKAGE_NAME))
            methodRunner = MethodRunner(loader.classLoader()) { loader.load(it) }
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
        methodRunner.run(CLASS_NAME, methodName)
        assertTrue(doneMethods.contains(methodName))
    }

    @Test
    fun simpleCoverageTest() {
        val result = methodRunner.run(CLASS_NAME, "simpleCoverageTest")
        assertEquals(2, result.totalMethods - result.missedMethods)
        assertEquals(4, result.totalLines - result.missedLines)
        assertEquals(0, result.totalBranches - result.missedBranches)
    }

    @Test
    fun coverageTest() {
        val result = methodRunner.run(CLASS_NAME, "coverageTest")
        assertEquals(2, result.totalMethods - result.missedMethods)
        assertTrue(2 <= result.totalBranches - result.missedBranches)
        assertTrue(5 >= result.totalBranches - result.missedBranches)
    }

}
