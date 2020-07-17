package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMinimizer
import kotlinx.fuzzer.fuzzing.storage.Storage
import kotlinx.fuzzer.fuzzing.storage.exceptions.StacktraceEqualException
import kotlinx.fuzzer.fuzzing.storage.exceptions.strackTraceEqualTo

class FailInput(data: ByteArray, val e: Throwable) : Input(data) {
    /** A minimization is possible if minimized input fails with the same exception. */
    override fun minimize(coverageRunner: CoverageRunner, targetMethod: TargetMethod) =
        InputMinimizer<FailInput>(coverageRunner, targetMethod).minimize(this)

    override fun save(storage: Storage): Input = this.also {
        storage.save(this)
    }

    override fun hashCode() = StacktraceEqualException(e).hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is FailInput) return false
        return e.strackTraceEqualTo(other.e)
    }
}
