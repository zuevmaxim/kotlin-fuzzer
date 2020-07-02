package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMinimizer
import kotlinx.fuzzer.fuzzing.storage.Storage
import kotlinx.fuzzer.fuzzing.storage.exceptions.*

class FailInput(data: ByteArray, val e: Throwable) : Input(data) {
    /** A minimization is possible if minimized input fails with the same exception. */
    override fun minimize(coverageRunner: CoverageRunner, targetMethod: TargetMethod) =
            InputMinimizer<FailInput>(coverageRunner, targetMethod).minimize(this) { newInput ->
                when (newInput) {
                    is FailInput -> e.stackTraceEqualTo(newInput.e)
                    else -> false
                }
            }

    override fun save(storage: Storage): Input = this.also {
        storage.save(this)
    }
}
