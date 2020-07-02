package kotlinx.fuzzer

import kotlinx.fuzzer.fuzzing.inputhandlers.CorpusInputTask
import kotlinx.fuzzer.fuzzing.inputhandlers.MutationTask
import kotlinx.fuzzer.fuzzing.log.Logger
import kotlinx.fuzzer.fuzzing.log.TasksLog
import kotlinx.fuzzer.fuzzing.storage.ContextFactory
import kotlinx.fuzzer.fuzzing.storage.Storage
import kotlinx.fuzzer.fuzzing.storage.createStorageStrategy
import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class Fuzzer(arguments: FuzzerArgs) {
    private val threadPool = newFixedBlockingQueueThreadPool(arguments.threadsNumber, arguments.maxTaskQueueSize)

    // lazy helps handle with cyclic dependency between Logger and Storage
    private val logger: Logger by lazy {
        val log = TasksLog(threadPool, arguments.maxTaskQueueSize)
        Logger(storage, stop, File(arguments.workingDirectory), log)
    }
    private val storage = Storage(File(arguments.workingDirectory), arguments.storageStrategy) { logger }
    private val contextFactory = ContextFactory(this, storage, arguments)
    private val mutationTask = MutationTask(this, storage, contextFactory)
    private val stop = AtomicBoolean(false)
    private var exception: Throwable? = null

    constructor(clazz: Class<*>) : this(classToArgs(clazz))

    fun start() {
        mutationTask.start()
        storage.listCorpusInput().map { CorpusInputTask(contextFactory, it) }.forEach { submit(it) }
        submit(Runnable { logger.log("All init corpus submitted") })
        runCatching { logger.run() }.onFailure { e -> stop(e) }
        exception?.let { throw it }
    }

    internal fun submit(task: Runnable) {
        threadPool.execute(task)
    }

    internal fun stop(exception: Throwable?) {
        if (!stop.compareAndSet(false, true)) return
        threadPool.shutdownNow()
        mutationTask.stop()
        this.exception = exception
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

        fun classToArgs(clazz: Class<*>): FuzzerArgs {
            val method = clazz.declaredMethods
                .singleOrNull { it.getAnnotation(Fuzz::class.java) != null }
                ?: throw IllegalArgumentException("One method with Fuzz annotation expected.")
            val annotation = method.getAnnotation(Fuzz::class.java)!!
            val storageStrategy = createStorageStrategy(clazz, annotation.workingDirectory)
            val className = clazz.name
            val packageName = clazz.packageName
            return FuzzerArgs(
                className = className,
                methodName = method.name,
                workingDirectory = annotation.workingDirectory,
                classpath = annotation.classpath.toList(),
                packages = annotation.packages.asSequence().plus(packageName).toList(),
                maxTaskQueueSize = annotation.maxTaskQueueSize,
                storageStrategy = storageStrategy
            )
        }
    }
}
