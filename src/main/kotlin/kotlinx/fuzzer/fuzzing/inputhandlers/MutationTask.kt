package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.fuzzing.storage.Storage
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/** Takes inputs from corpus, mutates them and submit new tasks. */
class MutationTask(private val fuzzer: Fuzzer, private val storage: Storage, context: FuzzerContext) : Runnable {
    /** A flag to stop this thread. */
    private val stop = AtomicBoolean(false)

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()
    private val mutator = InputMutator(fuzzer, storage, context, CORPUS_INPUT_MUTATION_COUNT)

    /** Wake up task is used to continue mutating when fuzzer task queue becomes empty. */
    private val wakeUpTask = Runnable {
        lock.withLock {
            condition.signal()
        }
    }

    /** Start mutator on a new thread. */
    fun start() {
        Thread(this).apply {
            setUncaughtExceptionHandler { _, e -> fuzzer.stop(e) }
            start()
        }
    }

    /** Raise flag to stop execution. */
    fun stop() {
        stop.set(true)
        lock.withLock {
            condition.signal()
        }
    }

    override fun run() = lock.withLock {
        while (!stop.get()) {
            val input = storage.corpusInputs.next() ?: continue
            mutator.mutate(input)
            fuzzer.submit(wakeUpTask)
            // If fuzzer uses bounded queue the task might be ignored.
            // Use await with timeout in this case to make sure that mutations will go on.
            condition.await()
        }
    }
}

/**
 * This constant regulates how fast fuzzer's task queue becomes empty.
 * If it is too big queue will be filled with outdated inputs.
 * If it is too small there will be a big overhead in thread congestion.
 */
private const val CORPUS_INPUT_MUTATION_COUNT = 150
