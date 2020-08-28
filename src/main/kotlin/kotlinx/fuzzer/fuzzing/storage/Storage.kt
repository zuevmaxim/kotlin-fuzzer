package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.coverage.CoverageResult
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Hash
import kotlinx.fuzzer.fuzzing.input.Input
import kotlinx.fuzzer.fuzzing.storage.exceptions.ExceptionsStorage
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class Storage(private val fuzzer: Fuzzer, workingDirectory: File, private val strategy: StorageStrategy) {
    // lazy helps handle with cyclic dependency between Logger and Storage
    private val logger by lazy { fuzzer.logger }
    private val init = FileStorage(workingDirectory, "init")
    private val exceptionsStorage = ExceptionsStorage()
    private val savedCrashes = AtomicInteger(0)

    val bestCoverage = AtomicReference(CoverageResult.default)
    val corpusInputs = CorpusStorage(fuzzer.arguments.corpusMemoryLimitMb)

    val corpusCount: Int
        get() = corpusInputs.size

    val crashesCount: Int
        get() = savedCrashes.get()

    init {
        val corpusContent = init.listFilesContent()
        if (corpusContent == null || corpusContent.isEmpty()) {
            val data = ByteArray(0)
            init.saveInput(data, Hash(data))
        }
    }

    fun save(input: ExecutedInput) {
        var current: CoverageResult
        do {
            current = bestCoverage.get()
        } while (isBestInput(input, current) && !bestCoverage.compareAndSet(current, input.coverageResult))
        if (corpusInputs.add(input)) {
            strategy.save(input)
        }
    }

    fun save(input: FailInput) {
        if (!exceptionsStorage.tryAdd(input.e)) return
        val minimized = minimizeInput(input)
        val hash = Hash(minimized.data)
        if (strategy.save(minimized, hash)) {
            savedCrashes.incrementAndGet()
            logger.log(minimized.e, hash)
        }
    }

    fun listCorpusInput() = init.listFilesContent()?.map { Input(it) } ?: emptyList()

    private fun isBestInput(input: ExecutedInput, current: CoverageResult) = current < input.coverageResult

    private inline fun <reified T : Input> minimizeInput(input: T): T {
        val context = fuzzer.context
        val minimized = input.minimize(context.minimizer)
        check(minimized is T) { "Minimization should not change type." }
        return minimized
    }

}
