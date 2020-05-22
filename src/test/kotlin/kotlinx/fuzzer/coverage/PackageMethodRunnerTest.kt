package kotlinx.fuzzer.coverage

import kotlinx.fuzzer.classload.Loader
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.Input
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.ByteBuffer
import java.util.stream.Stream

internal class PackageMethodRunnerTest {
    companion object {
        val doneMethods = hashSetOf<String>()

        private const val CLASS_LOCATION = "build/classes/kotlin/test/ru/example/kotlinfuzzer/testclasses/packagetest/"
        private const val PACKAGE_NAME = "kotlinx.fuzzer.testclasses.packagetest"
        private const val CLASS_NAME = "kotlinx.fuzzer.testclasses.packagetest.TestClassB"
        private lateinit var methodRunner: MethodRunner
        private lateinit var targetClass: Class<*>

        @JvmStatic
        @BeforeAll
        fun init() {
            val loader = Loader(listOf(CLASS_LOCATION), listOf(
                PACKAGE_NAME
            ))
            methodRunner = MethodRunner { loader.load(it) }
            targetClass = loader.classLoader().loadClass(
                CLASS_NAME
            ) ?: error("Class $CLASS_NAME not found.")
        }

        @JvmStatic
        @AfterAll
        fun finish() {
            methodRunner.shutdown()
        }

        @JvmStatic
        private fun provideArgs(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(0, 0, 0, 9, 1),
                Arguments.of(1, 2, -1, 10, 2),
                Arguments.of(-1, -3, 1, 11, 3)
            )
        }
    }

    @Test
    fun runMethodTest() {
        val methodName = "testRunning"
        val targetMethod = TargetMethod(targetClass, methodName)
        assertFalse(doneMethods.contains(methodName))
        methodRunner.run {
            targetMethod.execute(Input(ByteArray(0))) {
                assertTrue(it.isSuccess)
                assertEquals(1, it.getOrNull())
            }
        }
        assertTrue(doneMethods.contains(methodName))
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    fun coverageTest(x: Int, y: Int, returnValue: Int, lines: Int, branches: Int) {
        val methodName = "coverageTest"
        val bytes = ByteArray(8).also {
            ByteBuffer.wrap(it).putInt(x).putInt(y)
        }
        val targetMethod = TargetMethod(targetClass, methodName)
        val result = methodRunner.run {
            targetMethod.execute(Input(bytes)) {
                assertTrue(it.isSuccess)
                assertEquals(returnValue, it.getOrNull())
            }
        }
        assertEquals(4, result.totalMethods - result.missedMethods)
        assertEquals(lines, result.totalLines - result.missedLines)
        assertEquals(branches, result.totalBranches - result.missedBranches)
    }

}
