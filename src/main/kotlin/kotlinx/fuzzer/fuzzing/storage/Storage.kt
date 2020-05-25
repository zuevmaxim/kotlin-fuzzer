package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.coverage.CoverageResult
import kotlinx.fuzzer.fuzzing.Logger
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Hash
import kotlinx.fuzzer.fuzzing.input.Input
import org.apache.commons.lang3.ObjectUtils.max
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Storage(workingDirectory: File, getLogger: () -> Logger) {
    private val logger by lazy { getLogger() }
    private val init = FileStorage(workingDirectory, "init")
    private val lock = ReentrantLock()
    private val _corpusInputs = mutableListOf<ExecutedInput>()

    val crashes = FileStorage(workingDirectory, "crashes")
    val corpus = FileStorage(workingDirectory, "corpus")
    var bestCoverage = CoverageResult(1, 1, 1, 1, 1, 1)
        private set
    val corpusInputs: List<ExecutedInput> = _corpusInputs
    var crashesCount: Int = 0
        private set

    init {
        val corpusContent = init.listFilesContent()
        if (corpusContent == null || corpusContent.isEmpty()) {
            val data = ByteArray(0)
            init.saveInput(data, Hash(data))
        }
    }

    internal fun addCorpusInputs(
        inputs: Collection<ExecutedInput>,
        localMaxCoverage: CoverageResult,
        localCrashesCount: Int
    ) = lock.withLock {
        _corpusInputs.addAll(inputs)
        crashesCount += localCrashesCount
        bestCoverage = max(bestCoverage, localMaxCoverage)
    }

    internal fun save(input: ExecutedInput) = corpus.save(input)

    internal fun save(input: FailInput) = crashes.save(input)
        .also { if (it) logger.log(input) }

    fun listCorpusInput() = init.listFilesContent()?.map { Input(it) } ?: emptyList()

}
