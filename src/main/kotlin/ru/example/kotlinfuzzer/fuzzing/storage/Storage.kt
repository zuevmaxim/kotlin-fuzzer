package ru.example.kotlinfuzzer.fuzzing.storage

import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.TargetMethod
import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.input.Hash
import ru.example.kotlinfuzzer.fuzzing.input.Input
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class Storage(
    val targetMethod: TargetMethod,
    val methodRunner: MethodRunner,
    workingDirectory: File
) {

    private val crashes = FileStorage(workingDirectory, "crashes")
    private val corpus = FileStorage(workingDirectory, "corpus")
    private val bestPriority = AtomicInteger(0)

    init {
        val corpusContent = corpus.listFilesContent()
        if (corpusContent == null || corpusContent.isEmpty()) {
            val data = ByteArray(0)
            corpus.saveInput(data, Hash(data))
        }
    }

    fun isAlreadyExecuted(input: Input): Boolean {
        return false
    }

    fun save(input: ExecutedInput) {
        if (input.priority() > bestPriority.get()) {
            bestPriority.set(input.priority())
            corpus.save(input)
        }

    }
    fun save(input: FailInput) = crashes.save(input)

    fun markExecuted(hash: Hash) {

    }

    fun listCorpusInput() = corpus.listFilesContent()?.map { Input(it) } ?: emptyList()

}
