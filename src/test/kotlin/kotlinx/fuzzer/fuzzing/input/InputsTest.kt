package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.SingleClassCoverageRunnerTest
import kotlinx.fuzzer.fuzzing.TargetMethod
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class InputsTest {
    companion object {
        private val coverageRunner = SingleClassCoverageRunnerTest.coverageRunner
        private val targetClass = SingleClassCoverageRunnerTest.targetClass
    }

    @Test
    fun crashTest() {
        val methodName = "failTest"
        val targetMethod = TargetMethod(targetClass, methodName)
        val failInput = Input(ByteArray(1)).run(coverageRunner, targetMethod) as FailInput
        val e = failInput.e
        assertEquals(IllegalStateException::class, e::class)
        assertEquals("Crash", e.message)

        val minimized = failInput.minimize(coverageRunner, targetMethod)
        assertEquals(IllegalStateException::class, minimized.e::class)
        assertEquals("Crash", minimized.e.message)
        assertTrue(ByteArray(0).contentEquals(minimized.data))
    }

    @Test
    fun executedTest() {
        val methodName = "simpleTest"
        val targetMethod = TargetMethod(targetClass, methodName)
        val executed = Input(ByteArray(1)).run(coverageRunner, targetMethod) as ExecutedInput
        assertEquals(1, executed.userPriority)

        val minimized = executed.minimize(coverageRunner, targetMethod)
        assertEquals(1, minimized.userPriority)
        assertEquals(executed.coverageResult, minimized.coverageResult)
        assertTrue(ByteArray(0).contentEquals(minimized.data))
    }

    @Test
    fun impossibleFailMinimizationTest() {
        val methodName = "conditionTest"
        val targetMethod = TargetMethod(targetClass, methodName)

        for (size in 1..2) {
            val failInput = Input(ByteArray(size)).run(coverageRunner, targetMethod) as FailInput
            val minimized = failInput.minimize(coverageRunner, targetMethod)
            assertSame(failInput, minimized)
        }
    }

    @Test
    fun impossibleExecutedMinimizationTest() {
        val methodName = "conditionTest"
        val targetMethod = TargetMethod(targetClass, methodName)

        val size = 4
        val executed = Input(ByteArray(size)).run(coverageRunner, targetMethod) as ExecutedInput
        val minimized = executed.minimize(coverageRunner, targetMethod)
        assertSame(executed, minimized)
    }
}
