package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

/** Takes inputs from corpus, mutates them and submit new tasks. */
class MutationTask(private val fuzzer: Fuzzer, private val storage: Storage) : Runnable {

    fun start() {
        Thread(this).start()
    }

    override fun run() {
        val handlers = Handlers(storage, fuzzer, 150)
        while (true) {
            Thread.sleep(1000)
            if (storage.corpusInputs.isEmpty()) continue
            val input = storage.corpusInputs.last()
            handlers.mutator.mutate(input)
        }
    }
}
