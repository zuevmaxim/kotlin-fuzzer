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
import java.util.*

class IntegrationTest {
    companion object {
        private const val directory = "test-project/results/kotlinx.fuzzer.tests.apache.zip.ApacheZipTest.test"

        lateinit var fuzzer: Fuzzer

        private fun clearWorkingDirectory() {
            File(directory, "log.txt").delete()
            File(directory, "corpus").deleteRecursively()
            File(directory, "crashes").deleteRecursively()
        }

        @JvmStatic
        @BeforeAll
        fun before() {
            assertEquals(
                0,
                Runtime.getRuntime().exec(arrayOf("test-project/gradlew", "jar", "-p", "test-project")).waitFor()
            )
            clearWorkingDirectory()
            val args = FuzzerArgs(
                className = "kotlinx.fuzzer.tests.apache.zip.ApacheZipTest",
                methodName = "test",
                workingDirectory = directory,
                classpath = listOf("test-project/build/libs/test-project-1.0-SNAPSHOT-all.jar"),
                packages = listOf("kotlinx.fuzzer.tests.apache.zip", "org.apache.commons.compress")
            )
            fuzzer = Fuzzer(args)
            fuzzer.start(10)
            println("Fuzz finished")
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
        val storage = fuzzer.context.storage
        val storageCorpus = HashSet(storage.corpusInputs)
        val corpusCount = storage.corpusCount
        assertEquals(corpusCount, storageCorpus.size)
        val executed = File(directory, "corpus").listFiles()!!
            .map { it.readBytes() }
            .map { InputRunner.executeInput(coverageRunner, targetMethod, Input(it)) }
            .map { assertTrue(it is ExecutedInput); it as ExecutedInput }
        assertEquals(corpusCount, executed.size)
        val executedSet = executed.toHashSet()
        assertEquals(corpusCount, executedSet.size)
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
