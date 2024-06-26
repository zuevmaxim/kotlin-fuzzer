package kotlinx.fuzzer.coverage

import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.Input
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.nio.ByteBuffer
import java.util.stream.Stream


@Suppress("unused", "UNUSED_PARAMETER")
internal class SingleClassCoverageRunnerTest {
    companion object {
        val doneMethods = hashSetOf<String>()

        private const val CLASS_LOCATION = "build/classes/kotlin/test/kotlinx/fuzzer/testclasses/singleclasstest/"
        private const val PACKAGE_NAME = "kotlinx.fuzzer.testclasses.singleclasstest"
        private const val CLASS_NAME = "kotlinx.fuzzer.testclasses.singleclasstest.TestClass"
        internal val coverageRunner = createCoverageRunner(listOf(CLASS_LOCATION), listOf(PACKAGE_NAME))
        internal val targetClass = coverageRunner.loadClass(CLASS_NAME) ?: error("Class $CLASS_NAME not found.")


        @JvmStatic
        private fun provideArgs(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(0, 0, 1, 2),
                Arguments.of(0, 1, 2, 4),
                Arguments.of(1, 0, 3, 4),
                Arguments.of(1, 1, 4, 4)
            )
        }
    }

    @Test
    fun runMethodTest() {
        val methodName = "testRunning"
        val targetMethod = TargetMethod(targetClass, methodName)
        assertFalse(doneMethods.contains(methodName))
        coverageRunner.runWithCoverage {
            targetMethod.execute(Input(ByteArray(0))).also {
                assertTrue(it.isSuccess)
                assertEquals(1, it.getOrNull())
            }
        }
        assertTrue(doneMethods.contains(methodName))
    }

    @Test
    fun simpleCoverageTest() {
        val methodName = "simpleCoverageTest"
        val targetMethod = TargetMethod(targetClass, methodName)
        val result = coverageRunner.runWithCoverage {
            targetMethod.execute(Input(ByteArray(0))).also {
                assertTrue(it.isSuccess)
            }
        }
        assertEquals(0.0, result.score())
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    fun coverageTest(x: Int, y: Int, returnValue: Int, branches: Int) {
        val methodName = "coverageTest"
        val bytes = ByteArray(8).also {
            ByteBuffer.wrap(it).putInt(x).putInt(y)
        }
        val targetMethod = TargetMethod(targetClass, methodName)
        val result = coverageRunner.runWithCoverage {
            targetMethod.execute(Input(bytes)).also {
                assertTrue(it.isSuccess)
                assertEquals(returnValue, it.getOrNull())
            }
        }
        assertEquals(branches.toDouble(), result.score())
    }

    @Test
    fun coverageResultTest() {
        val methodName = "coverageTest"
        fun bytes(x: Int, y: Int) = ByteArray(8).also {
            ByteBuffer.wrap(it).putInt(x).putInt(y)
        }

        val targetMethod = TargetMethod(targetClass, methodName)
        val result1 = coverageRunner.runWithCoverage { targetMethod.execute(Input(bytes(0, 0))) }
        val result2 = coverageRunner.runWithCoverage { targetMethod.execute(Input(bytes(1, 1))) }
        assertTrue(result1 < result2)
        assertTrue(result1.otherCoverageRatio(result2) < 1)
        assertTrue(result2.otherCoverageRatio(result1) > 1)
    }

    class TestInvalidMethodClass {
        fun noArgs(): Int = 1
        fun nonIntReturnValue(bytes: ByteArray) = 1L
        fun noReturnValue(bytes: ByteArray) {}
        fun twoArgs(bytes: ByteArray, bytes2: ByteArray) = 1
        fun invalidArgType(x: Int) = 1
    }

    @ParameterizedTest
    @ValueSource(strings = ["noArgs", "nonIntReturnValue", "noReturnValue", "twoArgs", "invalidArgType"])
    fun invalidTargetMethodTest(methodName: String) {
        assertThrows(IllegalArgumentException::class.java) { TargetMethod(targetClass, methodName) }
    }
}
