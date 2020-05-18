package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.coverage.CoverageResult
import kotlinx.fuzzer.fuzzing.Logger
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Hash
import kotlinx.fuzzer.fuzzing.input.Input
import java.io.File
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicReference

class Storage(workingDirectory: File, getLogger: () -> Logger) {
    private val logger by lazy { getLogger() }

    val crashes = FileStorage(workingDirectory, "crashes")
    val corpus = FileStorage(workingDirectory, "corpus")
    val executed = FileStorage(workingDirectory, "executed")
    val bestCoverage = AtomicReference(CoverageResult(1, 1, 1, 1, 1, 1))
    val corpusInputs = ConcurrentSkipListSet<ExecutedInput> { inputA, inputB ->
        inputA.priority().compareTo(inputB.priority())
    }

    init {
        val corpusContent = corpus.listFilesContent()
        if (corpusContent == null || corpusContent.isEmpty()) {
            val data = ByteArray(0)
            corpus.saveInput(data, Hash(data))
        }
    }

    /** Save input to corpus if it's score is higher then current maximum. */
    fun save(input: ExecutedInput, force: Boolean = false) {
        var current: CoverageResult
        do {
            current = bestCoverage.get()
        } while (!force && isBestInput(input, current) && !bestCoverage.compareAndSet(current, input.coverageResult))
        if (force || isBestInput(input, current)) {
            corpus.save(input)
            corpusInputs.add(input)
        }
    }

    fun save(input: FailInput) {
        logger.log(input)
        crashes.save(input)
    }

    fun listCorpusInput() = corpus.listFilesContent()?.map { Input(it) } ?: emptyList()

    fun isBestInput(input: ExecutedInput) = isBestInput(input, bestCoverage.get())

    private fun isBestInput(input: ExecutedInput, current: CoverageResult) = current < input.coverageResult

}
