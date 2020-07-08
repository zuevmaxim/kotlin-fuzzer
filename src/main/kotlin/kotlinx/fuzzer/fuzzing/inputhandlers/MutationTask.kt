package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.fuzzing.storage.Storage
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/** Takes inputs from corpus, mutates them and submit new tasks. */
class MutationTask(private val fuzzer: Fuzzer, private val storage: Storage, context: FuzzerContext) : Runnable {
    private val stop = AtomicBoolean(false)
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()
    private val mutator = InputMutator(fuzzer, storage, context, 150)
    private val wakeUpTask = Runnable {
        lock.withLock {
            condition.signal()
        }
    }

    fun start() {
        Thread(this).apply {
            setUncaughtExceptionHandler { _, e -> fuzzer.stop(e) }
            start()
        }
    }

    fun stop() {
        stop.set(true)
        lock.withLock {
            condition.signal()
        }
    }

    override fun run() = lock.withLock {
        while (!stop.get()) {
            if (storage.corpusInputs.size == 0) continue
            val input = storage.corpusInputs.random()
            mutator.mutate(input)
            fuzzer.submit(wakeUpTask)
            condition.await(MAX_SLEEP_TIME_S, TimeUnit.SECONDS)
        }
    }
}

private const val MAX_SLEEP_TIME_S = 3L
