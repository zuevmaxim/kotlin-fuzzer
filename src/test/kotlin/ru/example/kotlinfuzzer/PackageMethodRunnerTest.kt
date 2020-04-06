package ru.example.kotlinfuzzer

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.example.kotlinfuzzer.classload.Loader
import ru.example.kotlinfuzzer.coverage.MethodRunner

internal class PackageMethodRunnerTest {
    companion object {
        val doneMethods = hashSetOf<String>()
        private const val CLASS_LOCATION =
            "build/classes/kotlin/test/ru/example/kotlinfuzzer/testclasses/packagetest/"
        private const val PACKAGE_NAME = "ru.example.kotlinfuzzer.testclasses.packagetest"
        private const val CLASS_NAME = "ru.example.kotlinfuzzer.testclasses.packagetest.TestClassB"
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
    fun coverageTest() {
        val result = methodRunner.run(CLASS_NAME, "coverageTest")
        assertEquals(4, result.totalMethods - result.missedMethods)
    }

}
