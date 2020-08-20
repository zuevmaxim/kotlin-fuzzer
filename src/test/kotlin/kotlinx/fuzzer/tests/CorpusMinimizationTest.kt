package kotlinx.fuzzer.tests

import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.FuzzerArgs
import kotlinx.fuzzer.fuzzing.CorpusMinimizer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class CorpusMinimizationTest {
    companion object {
        private const val directory = "test-project/results/kotlinx.fuzzer.tests.simple.abcd.ABCDTest.test"

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
                className = "kotlinx.fuzzer.tests.simple.abcd.ABCDTest",
                methodName = "test",
                workingDirectory = directory,
                classpath = listOf("test-project/build/libs/test-project-1.0-SNAPSHOT-all.jar"),
                packages = listOf("kotlinx.fuzzer.tests.simple.abcd")
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


    @Test
    fun corpusMinimizationCreatesResultingDirectory() {
        val corpus = File(directory, "corpus")
        val beforeFiles = corpus.listFiles()!!.map { it.name }.toHashSet()
        CorpusMinimizer(
            "kotlinx.fuzzer.tests.simple.abcd.ABCDTest",
            "test",
            listOf("test-project/build/libs/test-project-1.0-SNAPSHOT-all.jar"),
            listOf("kotlinx.fuzzer.tests.simple.abcd")
        ).minimize(corpus)
        val afterFiles = corpus.listFiles()!!.map { it.name }.toHashSet()
        assertTrue(afterFiles.remove("minimized"))
        assertEquals(beforeFiles, afterFiles)
    }

}
