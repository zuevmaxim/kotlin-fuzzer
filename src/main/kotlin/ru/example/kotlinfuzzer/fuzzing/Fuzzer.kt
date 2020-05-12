package ru.example.kotlinfuzzer.fuzzing

import ru.example.kotlinfuzzer.cli.CommandLineArgs
import ru.example.kotlinfuzzer.exceptions.ExceptionHandlingThreadFactory
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.CorpusInputTask
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.MutationTask
import ru.example.kotlinfuzzer.fuzzing.storage.ContextFactory
import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class Fuzzer(arguments: CommandLineArgs) {
    private val threadPool = Executors.newFixedThreadPool(arguments.threadsNumber(), ExceptionHandlingThreadFactory(this))
    private val storage: Storage = Storage(File(arguments.workingDirectory))
    private val contextFactory = ContextFactory(this, storage, arguments)
    private val mutationTask = MutationTask(this, storage, contextFactory)

    fun start() {
        mutationTask.start()
        storage.listCorpusInput().map { CorpusInputTask(contextFactory, it) }.forEach { submit(it) }
    }

    fun submit(task: Runnable) {
        threadPool.execute(task)
    }

    private val stop = AtomicBoolean(false)
    fun stop(exception: Throwable?) {
        if (!stop.compareAndSet(false, true)) return
        threadPool.shutdown()
        mutationTask.stop()
        println("${exception?.javaClass?.name}: ${exception?.localizedMessage}")
    }
}
