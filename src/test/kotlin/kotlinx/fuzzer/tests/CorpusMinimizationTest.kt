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
        private const val directory = "testC"

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
                packages = listOf("kotlinx.fuzzer.testclasses.coverage"),
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


    @Test
    fun corpusMinimizationCreatesResultingDirectory() {
        val corpus = File(directory, "corpus")
        val beforeFiles = corpus.listFiles()!!.map { it.name }.toHashSet()
        CorpusMinimizer(
            "kotlinx.fuzzer.testclasses.coverage.ABCD",
            "test",
            emptyList(),
            listOf("kotlinx.fuzzer.testclasses.coverage")
        ).minimize(corpus)
        val afterFiles = corpus.listFiles()!!.map { it.name }.toHashSet()
        assertTrue(afterFiles.remove("minimized"))
        assertEquals(beforeFiles, afterFiles)
    }

}
