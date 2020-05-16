package ru.example.kotlinfuzzer.fuzzing

import ru.example.kotlinfuzzer.cli.CommandLineArgs
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.CorpusInputTask
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.MutationTask
import ru.example.kotlinfuzzer.fuzzing.storage.ContextFactory
import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class Fuzzer(arguments: CommandLineArgs) {
    private val threadPool = Executors.newFixedThreadPool(arguments.threadsNumber()) { runnable ->
        Thread(runnable).apply {
            setUncaughtExceptionHandler { _, e -> stop(e) }
        }
    }
    private val storage: Storage = Storage(File(arguments.workingDirectory))
    private val contextFactory = ContextFactory(this, storage, arguments)
    private val mutationTask = MutationTask(this, storage, contextFactory)
    private val stop = AtomicBoolean(false)
    private val logger = Logger(storage, stop)

    fun start() {
        mutationTask.start()
        storage.listCorpusInput().map { CorpusInputTask(contextFactory, it) }.forEach { submit(it) }
        logger.run()
    }

    fun submit(task: Runnable) {
        threadPool.execute(task)
    }

    private fun stop(exception: Throwable?) {
        if (!stop.compareAndSet(false, true)) return
        threadPool.shutdown()
        mutationTask.stop()
        exception?.printStackTrace()
    }
}
