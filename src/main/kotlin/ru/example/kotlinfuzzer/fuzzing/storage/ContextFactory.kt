package ru.example.kotlinfuzzer.fuzzing.storage

import ru.example.kotlinfuzzer.cli.CommandLineArgs
import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.Context
import java.util.concurrent.ConcurrentHashMap

/** Contains contexts for every calling thread. */
class ContextFactory(private val fuzzer: Fuzzer, private val storage: Storage, private val arguments: CommandLineArgs) {
    private val contexts = ConcurrentHashMap<Long, Context>()

    /** Return context unique for calling thread or create a new one. */
    fun acquire(): Context {
        val threadId = Thread.currentThread().id
        return contexts[threadId] ?: Context(storage, arguments, fuzzer, this).also {
            contexts[threadId] = it
        }
    }
}
