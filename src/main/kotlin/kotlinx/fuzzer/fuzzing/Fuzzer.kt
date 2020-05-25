package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.fuzzing.inputhandlers.CorpusInputTask
import kotlinx.fuzzer.fuzzing.inputhandlers.MutationTask
import kotlinx.fuzzer.fuzzing.inputhandlers.SynchronizationTask
import kotlinx.fuzzer.fuzzing.storage.ContextFactory
import kotlinx.fuzzer.fuzzing.storage.Storage
import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class Fuzzer(arguments: FuzzerArgs) {
    val logger: Logger by lazy { Logger(storage, stop, File(arguments.workingDirectory), this) }

    private val threadPool = newFixedBlockingQueueThreadPool(arguments.threadsNumber, arguments.maxTaskQueueSize)
    private val storage = Storage(File(arguments.workingDirectory)) { logger }
    private val contextFactory = ContextFactory(this, storage, arguments)
    private val mutationTask = MutationTask(this, storage, contextFactory)
    private val stop = AtomicBoolean(false)

    fun start() {
        logger.start()
        mutationTask.start()
        storage.listCorpusInput().map { CorpusInputTask(contextFactory, it) }.forEach { submit(it) }
        submit(Runnable { logger.log("All init corpus submitted") })
        runCatching { run() }.onFailure { stop(it) }
    }

    internal fun submit(task: Runnable) = threadPool.execute(task)

    fun stop(exception: Throwable?) {
        if (!stop.compareAndSet(false, true)) return
        threadPool.shutdownNow()
        mutationTask.stop()
        exception?.printStackTrace()
    }

    private fun run() {
        while (!stop.get()) {
            try {
                Thread.sleep(SYNCHRONIZATION_TIMEOUT_MS)
            } catch (e: InterruptedException) {
            }
            repeat(threadPool.maximumPoolSize) {
                threadPool.execute(SynchronizationTask(contextFactory))
            }
        }
    }

    private fun newFixedBlockingQueueThreadPool(threadsNumber: Int, queueSize: Int) = ThreadPoolExecutor(
        threadsNumber,
        threadsNumber,
        0L,
        TimeUnit.MILLISECONDS,
        ArrayBlockingQueue(queueSize),
        ThreadFactory { runnable ->
            Thread(runnable).apply {
                setUncaughtExceptionHandler { _, e -> stop(e) }
            }
        },
        ThreadPoolExecutor.DiscardPolicy()
    )

    companion object {
        const val MAX_TASK_QUEUE_SIZE = 500
        private const val SYNCHRONIZATION_TIMEOUT_MS = 1000L
    }
}
