package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.cli.CommandLineArgs
import kotlinx.fuzzer.fuzzing.inputhandlers.CorpusInputTask
import kotlinx.fuzzer.fuzzing.inputhandlers.MutationTask
import kotlinx.fuzzer.fuzzing.storage.ContextFactory
import kotlinx.fuzzer.fuzzing.storage.Storage
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class Fuzzer(arguments: CommandLineArgs) {
    private val threadPool = Executors.newFixedThreadPool(arguments.threadsNumber()) { runnable ->
        Thread(runnable).apply {
            setUncaughtExceptionHandler { _, e -> stop(e) }
        }
    }
    private val logger: Logger by lazy { Logger(storage, stop, File(arguments.workingDirectory)) }
    private val storage = Storage(File(arguments.workingDirectory)) { logger }
    private val contextFactory = ContextFactory(this, storage, arguments)
    private val mutationTask = MutationTask(this, storage, contextFactory)
    private val stop = AtomicBoolean(false)

    fun start() {
        mutationTask.start()
        storage.listCorpusInput().map { CorpusInputTask(contextFactory, it) }.forEach { submit(it) }
        submit(Runnable { logger.log("All init corpus submitted") })
        runCatching { logger.run() }.onFailure { e -> stop(e) }
    }

    fun submit(task: Runnable) {
        threadPool.execute(task)
    }

    fun stop(exception: Throwable?) {
        if (!stop.compareAndSet(false, true)) return
        threadPool.shutdownNow()
        mutationTask.stop()
        exception?.printStackTrace()
    }
}
