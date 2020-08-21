package kotlinx.fuzzer

import kotlinx.fuzzer.fuzzing.inputhandlers.FuzzerContext
import kotlinx.fuzzer.fuzzing.inputhandlers.InputTask
import kotlinx.fuzzer.fuzzing.inputhandlers.MutationTask
import kotlinx.fuzzer.fuzzing.log.Logger
import kotlinx.fuzzer.fuzzing.log.TasksLog
import kotlinx.fuzzer.fuzzing.storage.FilesStorageStrategy
import kotlinx.fuzzer.fuzzing.storage.Storage
import kotlinx.fuzzer.fuzzing.storage.createStorageStrategy
import java.io.File
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class Fuzzer(internal val arguments: FuzzerArgs) {
    private val threadPool = newFixedBlockingQueueThreadPool(arguments.threadsNumber, arguments.maxTaskQueueSize)

    // lazy helps handle with cyclic dependency between Logger and Storage
    internal val logger: Logger by lazy {
        val log = TasksLog(threadPool, arguments.maxTaskQueueSize)
        Logger(storage, stop, File(arguments.workingDirectory), log)
    }
    private val storage = Storage(
        this,
        File(arguments.workingDirectory),
        arguments.storageStrategy ?: FilesStorageStrategy(File(arguments.workingDirectory), arguments.saveCorpus)
    )
    internal val context = FuzzerContext(storage, arguments, this)
    private val mutationTask = MutationTask(this, storage, context)
    private val stop = AtomicBoolean(false)
    private var exception: Throwable? = null

    constructor(clazz: Class<*>) : this(classToArgs(clazz))

    fun start(timeout: Long? = null, unit: TimeUnit = TimeUnit.SECONDS) {
        mutationTask.start()
        storage.listCorpusInput().map { InputTask(context, it) }.forEach { submit(it) }
        submit(Runnable { logger.log("All init corpus submitted") })
        setUpTimeTimeout(timeout, unit)
        runCatching { logger.run() }.onFailure { e -> stop(e) }
        exception?.let { throw it }
    }

    internal fun submit(task: Runnable) {
        threadPool.execute(task)
    }

    internal fun stop(exception: Throwable?) {
        if (!stop.compareAndSet(false, true)) return
        threadPool.shutdown()
        mutationTask.stop()
        threadPool.awaitTermination(5, TimeUnit.SECONDS)
        this.exception = exception
    }

    private fun setUpTimeTimeout(timeout: Long?, unit: TimeUnit) {
        if (timeout == null) return
        val timeMillis = unit.toMillis(timeout)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                stop(null)
            }
        }, timeMillis)
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
        const val DEFAULT_SAVE_CORPUS = false
        const val MAX_TASK_QUEUE_SIZE = 500
        const val MAX_CORPUS_SIZE = 1000
    }
}

private fun classToArgs(clazz: Class<*>): FuzzerArgs {
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
