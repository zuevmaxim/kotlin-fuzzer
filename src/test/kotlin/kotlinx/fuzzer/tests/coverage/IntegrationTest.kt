package kotlinx.fuzzer.tests.coverage

import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.FuzzerArgs
import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Input
import kotlinx.fuzzer.fuzzing.inputhandlers.InputRunner
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class IntegrationTest {
    companion object {
        private const val directory = "testB"

        lateinit var fuzzer: Fuzzer

        private fun clearWorkingDirectory() {
            File(directory).deleteRecursively()
        }

        @JvmStatic
        @BeforeAll
        fun before() {
            clearWorkingDirectory()
            val args = FuzzerArgs(
                className = "kotlinx.fuzzer.testclasses.coverage.ABCD",
                methodName = "test",
                workingDirectory = directory,
                classpath = emptyList(),
                _packages = emptyList(),
                saveCorpus = true
            )
            fuzzer = Fuzzer(args)
            fuzzer.start(10)
        }

        @JvmStatic
        @AfterAll
        fun after() {
            clearWorkingDirectory()
        }
    }

    private fun testInputCoverageDoesNotChange(
        bytes: ByteArray,
        coverageRunner: CoverageRunner,
        targetMethod: TargetMethod
    ) {
        fun runInput(bytes: ByteArray) = InputRunner.executeInput(coverageRunner, targetMethod, Input(bytes))
        val result = (runInput(bytes) as ExecutedInput).coverageResult
        generateSequence { bytes }
            .take(1000)
            .map { runInput(it) }
            .map { assertTrue(it is ExecutedInput); it as ExecutedInput }
            .forEach { assertEquals(result, it.coverageResult) }
    }

    @Test
    fun testCoverageDoesNotChange() {
        val coverageRunner = fuzzer.context.coverageRunner
        val targetMethod = fuzzer.context.targetMethod
        val storageCorpus = fuzzer.context.storage.corpusInputs.toList()
        storageCorpus
            .map { InputRunner.executeInput(coverageRunner, targetMethod, Input(it.data)) }
            .map { assertTrue(it is ExecutedInput); it as ExecutedInput }
            .forEachIndexed { i, input ->
                if (storageCorpus[i].coverageResult != input.coverageResult) {
                    testInputCoverageDoesNotChange(input.data, coverageRunner, targetMethod)
                    assertTrue(false) { "Fuzzer execution changes coverage." }
                }
            }
    }

    @Test
    fun testCorpusInputsAreUnique() {
        val coverageRunner = fuzzer.context.coverageRunner
        val targetMethod = fuzzer.context.targetMethod
        val executed = File(directory, "corpus").listFiles()!!
            .map { it.readBytes() }
            .map { InputRunner.executeInput(coverageRunner, targetMethod, Input(it)) }
            .map { assertTrue(it is ExecutedInput); it as ExecutedInput }
        val executedSet = executed.toHashSet()
        assertEquals(executed.size, executedSet.size)
    }

    @Test
    fun testCrashesFail() {
        val coverageRunner = fuzzer.context.coverageRunner
        val targetMethod = fuzzer.context.targetMethod
        val count = File(directory, "crashes").listFiles()!!
            .filter { !it.name.endsWith(".txt") }
            .map { it.readBytes() }
            .map { InputRunner.executeInput(coverageRunner, targetMethod, Input(it)) }
            .map { assertTrue(it is FailInput); it as FailInput }
            .count()
        assertTrue(count > 0)
    }

}
