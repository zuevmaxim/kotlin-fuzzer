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
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Main fuzzer class.
 * Fuzzing consists of several stages.
 * Initialization - corpus inputs are loaded into task queue.
 * Main cycle is processed by several threads(see [InputTask]):
 * 1. Get task from task queue
 * 2. Execute fuzz method
 * 3. Mutate on success invocation. Mutations are produced on this stage in order to let one input pass through
 * a range of mutations. Each worker may produce only one mutation to avoid queue exponential growth(see [Mutation]).
 * 4. Save results. Successful results may be saved on demand into corpus directory. Failed results are reported
 * by saving into a file, callback invocation or an exception thrown (see [StorageStrategy]).
 *
 * One more thread is used to add the best corpus inputs into a task queue after some mutations(see [MutationTask]).
 * This task is separated from main workers to control the task queue size.
 *
 * Execution score is measured by coverage of executed code. It is assumed that the higher coverage score is,
 * the higher the chance to find a bug.
 *
 * Different mutations are applied to corpus inputs to find a bug. See full list of mutation in [MutationFactory].
 */
class Fuzzer(internal val arguments: FuzzerArgs) {
    internal val handler = Thread.UncaughtExceptionHandler { _, e -> stop(e) }
    private val threadPool = newThreadPool(arguments.threadsNumber, handler)

    // lazy helps handle with cyclic dependency between Logger and Storage
    internal val logger: Logger by lazy {
        val log = TasksLog(threadPool)
        Logger(storage, stop, File(arguments.workingDirectory), log)
    }
    private val storage = Storage(this, File(arguments.workingDirectory), arguments.createStorageStrategy())
    internal val context = FuzzerContext(storage, arguments, this)
    private val mutationTask = MutationTask(this, storage, context)
    private val stop = AtomicBoolean(false)
    private var exception: Throwable? = null

    /** Create fuzzer from class. This constructor is convenient for unit testing.
     * @param clazz class that contains method with [Fuzz] annotation
     * @param saveCrash whether to save crashes using [FilesStorageStrategy] or throw on crash,
     * used only when no method with [FuzzCrash] annotation found
     */
    constructor(clazz: Class<*>, saveCrash: Boolean = DEFAULT_SAVE_CRASHES) : this(classToArgs(clazz, saveCrash))

    fun start(timeout: Long? = null, unit: TimeUnit = TimeUnit.SECONDS) {
        mutationTask.start()
        storage.listCorpusInput().map { InputTask(context, it) }.forEach { submit(it) }
        submit { logger.log("All init corpus submitted") }
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

    private fun newThreadPool(threadsNumber: Int, handler: Thread.UncaughtExceptionHandler) = ForkJoinPool(
        threadsNumber,
        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
        handler,
        true // use queues in FIFO mode, significant for tasks order
    )

    companion object {
        const val DEFAULT_SAVE_CORPUS = false
        const val CORPUS_MEMORY_LIMIT_MB = 256

        /** A flag to save crashes while unit testing or throw on crash. */
        const val DEFAULT_SAVE_CRASHES = false
    }
}

inline fun <reified T> Fuzzer(saveCrash: Boolean = Fuzzer.DEFAULT_SAVE_CRASHES) = Fuzzer(T::class.java, saveCrash)

private fun classToArgs(clazz: Class<*>, saveCrash: Boolean): FuzzerArgs {
    val method = clazz.declaredMethods
        .singleOrNull { it.getAnnotation(Fuzz::class.java) != null }
        ?: throw IllegalArgumentException("One method with Fuzz annotation expected.")
    val annotation = method.getAnnotation(Fuzz::class.java)!!
    val storageStrategy = createStorageStrategy(clazz, annotation.workingDirectory, saveCrash)
    val className = clazz.name
    return FuzzerArgs(
        className = className,
        methodName = method.name,
        workingDirectory = annotation.workingDirectory,
        classpath = annotation.classpath.toList(),
        _packages = annotation.packages.toList(),
        storageStrategy = storageStrategy
    )
}

private fun FuzzerArgs.createStorageStrategy() =
    storageStrategy ?: FilesStorageStrategy(File(workingDirectory), saveCorpus)
