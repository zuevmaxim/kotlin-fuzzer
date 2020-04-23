package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.storage.ContextFactory
import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/** Takes inputs from corpus, mutates them and submit new tasks. */
class MutationTask(private val fuzzer: Fuzzer, private val storage: Storage, contextFactory: ContextFactory) : Runnable {

    fun start() {
        Thread(this).start()
    }

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()
    private val mutator = InputMutator(fuzzer, storage, contextFactory, 150)

    private val wakeUpTask = Runnable {
        lock.lock()
        condition.signal()
        lock.unlock()
    }

    override fun run() {
        lock.lock()
        while (true) {
            if (storage.corpusInputs.isEmpty()) continue
            val input = storage.corpusInputs.last()
            mutator.mutate(input)
            fuzzer.submit(wakeUpTask)
            condition.await(MAX_SLEEP_TIME_S, TimeUnit.SECONDS)
        }
        // lock.unlock()
    }

    companion object {
        private const val MAX_SLEEP_TIME_S = 3L
    }
}
