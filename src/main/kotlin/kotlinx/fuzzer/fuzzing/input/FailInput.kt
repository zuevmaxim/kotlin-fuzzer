package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.fuzzing.inputhandlers.InputMinimizer
import kotlinx.fuzzer.fuzzing.storage.Storage
import kotlinx.fuzzer.fuzzing.storage.exceptions.StacktraceEqualException
import kotlinx.fuzzer.fuzzing.storage.exceptions.stackTraceEqualTo

/** Input which execution threw exception. */
class FailInput(data: ByteArray, val e: Throwable) : Input(data) {
    /** A minimization is possible if minimized input fails with the same exception. */
    override fun minimize(minimizer: InputMinimizer) = minimizer.minimize(this)

    override fun save(storage: Storage): Input = this.also {
        storage.save(this)
    }

    /** Exception's stacktrace hashcode. */
    override fun hashCode() = StacktraceEqualException(e).hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is FailInput) return false
        return e.stackTraceEqualTo(other.e)
    }
}
