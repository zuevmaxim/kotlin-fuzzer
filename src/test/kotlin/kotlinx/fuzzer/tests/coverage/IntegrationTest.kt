package kotlinx.fuzzer.tests.coverage

import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.FuzzerArgs
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
                packages = listOf("kotlinx.fuzzer.tests.apache.zip", "org.apache.commons.compress"),
                threadsNumber = 1,
                compositeCoverageCount = 0
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

    @Test
    fun testCorpusInputsAreUnique() {
        val coverageRunner = fuzzer.context.coverageRunner
        val targetMethod = fuzzer.context.targetMethod
        val storage = fuzzer.context.storage
        val corpusInputs = hashSetOf<ExecutedInput>()
        File(directory, "corpus").listFiles()!!
            .map { it.readBytes() }
            .map { InputRunner.executeInput(coverageRunner, targetMethod, Input(it)) }
            .map { assertTrue(it is ExecutedInput); it as ExecutedInput }
            .onEach { assertTrue(storage.corpusInputs.contains(it)) { "Corpus input is not in storage." } }
            .forEach { input -> assertTrue(corpusInputs.add(input)) { "Corpus inputs have repeated coverage." } }
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
