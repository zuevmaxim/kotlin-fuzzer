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

    private val crashes = FileStorage(workingDirectory, "crashes")
    private val corpus = FileStorage(workingDirectory, "corpus")
    val bestCoverage = AtomicReference<CoverageResult>(CoverageResult(1, 1, 1, 1, 1, 1))
    val corpusInputs = ConcurrentSkipListSet<ExecutedInput> { inputA, inputB ->
        inputA.priority() - inputB.priority()
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
    fun save(input: ExecutedInput) {
        var current: CoverageResult
        do {
            current = bestCoverage.get()
        } while (current < input.coverageResult && !bestCoverage.compareAndSet(current, input.coverageResult))
        if (current < input.coverageResult) {
            println("Score update: ${String(input.data)} ${input.priority()}")
            corpus.save(input)
            corpusInputs.add(input)
        }
    }

    fun save(input: FailInput) {
        println("Crash found: ${String(input.data)}")
        crashes.save(input)
    }

    fun listCorpusInput() = corpus.listFilesContent()?.map { Input(it) } ?: emptyList()

}
