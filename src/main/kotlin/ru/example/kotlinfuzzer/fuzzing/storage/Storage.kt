package ru.example.kotlinfuzzer.fuzzing.storage

import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.TargetMethod
import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.input.Hash
import ru.example.kotlinfuzzer.fuzzing.input.Input
import java.io.File
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

class Storage(
    val targetMethod: TargetMethod,
    val methodRunner: MethodRunner,
    workingDirectory: File
) {

    private val crashes = FileStorage(workingDirectory, "crashes")
    private val corpus = FileStorage(workingDirectory, "corpus")
    val bestPriority = AtomicInteger(0)
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
        var current = bestPriority.get()
        while (current < input.priority() && !bestPriority.compareAndSet(current, max(current, input.priority()))) {
            current = bestPriority.get()
        }
        if (current < input.priority()) {
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
