package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.MethodRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMinimizer
import kotlinx.fuzzer.fuzzing.storage.Storage

class FailInput(data: ByteArray, val e: Throwable) : Input(data) {
    /** A minimization is possible if minimized input fails with the same exception. */
    override fun minimize(methodRunner: MethodRunner, targetMethod: TargetMethod) =
        InputMinimizer<FailInput>(methodRunner, targetMethod).minimize(this) { newInput ->
            when (newInput) {
                is FailInput -> this.e::class == newInput.e::class // TODO use unique exceptions check here, compare stacktrace
                else -> false
            }
        }

    override fun save(storage: Storage, force: Boolean): Input = this.also {
        storage.save(this)
    }
}
