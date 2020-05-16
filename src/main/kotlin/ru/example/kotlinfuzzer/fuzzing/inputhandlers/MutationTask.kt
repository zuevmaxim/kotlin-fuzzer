package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.storage.ContextFactory
import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/** Takes inputs from corpus, mutates them and submit new tasks. */
class MutationTask(private val fuzzer: Fuzzer, private val storage: Storage, contextFactory: ContextFactory) : Runnable {
    private val stop = AtomicBoolean(false)
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()
    private val mutator = InputMutator(fuzzer, storage, contextFactory, 150)
    private val wakeUpTask = Runnable {
        lock.withLock {
            condition.signal()
        }
    }

    fun start() {
        Thread(this).start()
    }

    fun stop() {
        stop.set(true)
        lock.withLock {
            condition.signal()
        }
    }

    override fun run() = lock.withLock {
        while (!stop.get()) {
            if (storage.corpusInputs.isEmpty()) continue
            val input = storage.corpusInputs.last()
            mutator.mutate(input)
            fuzzer.submit(wakeUpTask)
            condition.await(MAX_SLEEP_TIME_S, TimeUnit.SECONDS)
        }
    }
}

private const val MAX_SLEEP_TIME_S = 3L
