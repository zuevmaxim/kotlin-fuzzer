package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.createCoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMinimizer
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

internal class FailInputTest {
    companion object {
        private const val CLASS_LOCATION = "build/classes/kotlin/test/kotlinx/fuzzer/testclasses/minimization/"
        private const val PACKAGE_NAME = "kotlinx.fuzzer.testclasses.minimization"
        private const val CLASS_NAME = "kotlinx.fuzzer.testclasses.minimization.MinimizationTestClass"
        private val coverageRunner = createCoverageRunner(listOf(CLASS_LOCATION), listOf(PACKAGE_NAME))
        private val targetClass = coverageRunner.loadClass(CLASS_NAME) ?: error("Class $CLASS_NAME not found.")
        val targetMethod = TargetMethod(targetClass, "test")
        private val minimizer = InputMinimizer(coverageRunner, targetMethod)
    }

    @Test
    fun minimizationIsImpossibleIfStackTraceChanged() {
        val failInput = Input(ByteArray(2)).run(coverageRunner, targetMethod)
        val minimizedFailInput = failInput.minimize(minimizer)
        assertSame(failInput.data, minimizedFailInput.data)
    }
}
