package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.fuzzing.Fuzzer
import kotlinx.fuzzer.fuzzing.FuzzerArgs
import kotlinx.fuzzer.fuzzing.inputhandlers.Context

/** Contains contexts for every calling thread. */
class ContextFactory(private val fuzzer: Fuzzer, private val storage: Storage, private val arguments: FuzzerArgs) {
    private val localContext: ThreadLocal<Context> = ThreadLocal.withInitial {
        Context(storage, arguments, fuzzer, this)
    }

    /** Return context unique for calling thread or create a new one. */
    fun context(): Context = localContext.get()
}
