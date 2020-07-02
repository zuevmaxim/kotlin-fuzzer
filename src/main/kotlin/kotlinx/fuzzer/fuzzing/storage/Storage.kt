package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.coverage.CoverageResult
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Hash
import kotlinx.fuzzer.fuzzing.input.Input
import kotlinx.fuzzer.fuzzing.log.Logger
import kotlinx.fuzzer.fuzzing.storage.exceptions.ExceptionsStorage
import java.io.File
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicReference

class Storage(workingDirectory: File, private val strategy: StorageStrategy, getLogger: () -> Logger) {
    // lazy helps handle with cyclic dependency between Logger and Storage
    private val logger by lazy { getLogger() }
    private val init = FileStorage(workingDirectory, "init")
    private val exceptionsStorage = ExceptionsStorage()

    val bestCoverage = AtomicReference(CoverageResult(1, 1, 1, 1, 1, 1))
    val corpusInputs = ConcurrentSkipListSet<ExecutedInput> { inputA, inputB ->
        inputA.coverage.compareTo(inputB.coverage)
    }

    val corpusCount: Int
        get() = corpusInputs.size

    val crashesCount: Int
        get() = exceptionsStorage.size

    init {
        val corpusContent = init.listFilesContent()
        if (corpusContent == null || corpusContent.isEmpty()) {
            val data = ByteArray(0)
            init.saveInput(data, Hash(data))
        }
    }

    /** Save input to corpus if it's score is higher then current maximum. */
    fun save(input: ExecutedInput) {
        var current: CoverageResult
        do {
            current = bestCoverage.get()
        } while (isBestInput(input, current) && !bestCoverage.compareAndSet(current, input.coverageResult))
        if (isBestInput(input, current)) {
            strategy.save(input)
            corpusInputs.add(input)
        }
    }

    fun save(input: FailInput) {
        if (!exceptionsStorage.tryAdd(input.e)) return
        val hash = Hash(input.data)
        if (strategy.save(input, hash)) {
            logger.log(input, hash)
        }
    }

    fun listCorpusInput() = init.listFilesContent()?.map { Input(it) } ?: emptyList()

    fun isBestInput(input: ExecutedInput) = isBestInput(input, bestCoverage.get())

    private fun isBestInput(input: ExecutedInput, current: CoverageResult) = current < input.coverageResult

}
