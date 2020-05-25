package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.fuzzing.storage.ContextFactory

class SynchronizationTask(private val contextFactory: ContextFactory) : Runnable {
    override fun run() {
        val context = contextFactory.context()
        context.storage.synchronizeWithGlobalStorage()
    }
}
