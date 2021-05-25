package kotlinx.fuzzer.fuzzing.storage.exceptions

import kotlinx.fuzzer.coverage.createCoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Input
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class ReflectionStacktraceTest {
    companion object {
        private const val CLASS_LOCATION = "build/classes/kotlin/test/kotlinx/fuzzer/testclasses/reflection/"
        private const val PACKAGE_NAME = "kotlinx.fuzzer.testclasses.reflection"
        private const val CLASS_NAME = "kotlinx.fuzzer.testclasses.reflection.ReflectionTestClass"
        private val coverageRunner = createCoverageRunner(listOf(CLASS_LOCATION), listOf(PACKAGE_NAME))
        private val targetClass = coverageRunner.loadClass(CLASS_NAME) ?: error("Class $CLASS_NAME not found.")
        val targetMethod = TargetMethod(targetClass, "test")
    }

    @Test
    fun userReflectionShouldNotBeTrimmed() {
        val failInput1 = Input(ByteArray(2)).run(coverageRunner, targetMethod) as FailInput
        val failInput2 = Input(ByteArray(3)).run(coverageRunner, targetMethod) as FailInput
        assertFalse(failInput1.e.stackTraceEqualTo(failInput2.e))
    }
}
