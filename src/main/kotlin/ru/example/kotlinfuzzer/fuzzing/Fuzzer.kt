package ru.example.kotlinfuzzer.fuzzing

import ru.example.kotlinfuzzer.cli.CommandLineArgs
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.InputTask
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.MutationTask
import ru.example.kotlinfuzzer.fuzzing.storage.ContextFactory
import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import java.io.File
import java.util.concurrent.Executors

class Fuzzer(arguments: CommandLineArgs) {
    private val threadPool = Executors.newFixedThreadPool(arguments.threadsNumber())
    private val storage: Storage = Storage(File(arguments.workingDirectory))
    private val contextFactory = ContextFactory(this, storage, arguments)

    fun start() {
        MutationTask(this, storage, contextFactory).start()
        storage.listCorpusInput().map { InputTask(contextFactory, it) }.forEach { submit(it) }
    }

    fun submit(task: Runnable) {
        threadPool.execute(task)
    }
}
