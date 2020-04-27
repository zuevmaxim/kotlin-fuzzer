package ru.example.kotlinfuzzer.fuzzing.storage

import ru.example.kotlinfuzzer.cli.CommandLineArgs
import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.Context

/** Contains contexts for every calling thread. */
class ContextFactory(private val fuzzer: Fuzzer, private val storage: Storage, private val arguments: CommandLineArgs) {
    private val localContext: ThreadLocal<Context> = ThreadLocal.withInitial {
        Context(storage, arguments, fuzzer, this)
    }

    /** Return context unique for calling thread or create a new one. */
    fun acquire(): Context = localContext.get()
}
