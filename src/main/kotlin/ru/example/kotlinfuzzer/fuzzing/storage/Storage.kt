package ru.example.kotlinfuzzer.fuzzing.storage

import ru.example.kotlinfuzzer.coverage.CoverageResult
import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.input.Hash
import ru.example.kotlinfuzzer.fuzzing.input.Input
import java.io.File
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicReference

class Storage(workingDirectory: File) {

    val crashes = FileStorage(workingDirectory, "crashes")
    val corpus = FileStorage(workingDirectory, "corpus")
    val executed = FileStorage(workingDirectory, "executed")
    val bestCoverage = AtomicReference<CoverageResult>(CoverageResult(1, 1, 1, 1, 1, 1))
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

//    private val executedSet = ConcurrentHashMap<Hash, Int>()
//    fun isAlreadyExecuted(input: Input) = executedSet.contains(input.hash)
//    fun markExecuted(hash: Hash) {
//        executedSet[hash] = 1
//    }

    /** Save maximum score input. */
    fun save(input: ExecutedInput, force: Boolean = false) {
        var current: CoverageResult
        do {
            current = bestCoverage.get()
        } while (!force && current < input.coverageResult && !bestCoverage.compareAndSet(current, input.coverageResult))
        if (force || current < input.coverageResult) {
            corpus.save(input)
            corpusInputs.add(input)
        }
    }

    fun save(input: FailInput) {
        crashes.save(input)
    }

    fun listCorpusInput() = corpus.listFilesContent()?.map { Input(it) } ?: emptyList()

}
