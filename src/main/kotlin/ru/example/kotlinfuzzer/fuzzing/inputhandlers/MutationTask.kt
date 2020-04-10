package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

class MutationTask(private val fuzzer: Fuzzer, private val storage: Storage, private val handlersNet: HandlersNet) : Runnable {

    fun start() {
        Thread(this).start()
    }

    override fun run() {
        val mutator = InputMutator(fuzzer, handlersNet, 150)
        while (true) {
            Thread.sleep(1000)
            if (storage.corpusInputs.isEmpty()) continue
            val input = storage.corpusInputs.lastEntry().value
            mutator.run(input)
        }
    }
}
