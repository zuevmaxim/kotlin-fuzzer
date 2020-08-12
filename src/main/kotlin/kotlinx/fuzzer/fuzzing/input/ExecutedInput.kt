package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.CoverageResult
import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMinimizer
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMutator
import kotlinx.fuzzer.fuzzing.storage.Storage

class ExecutedInput(
    data: ByteArray,
    val coverageResult: CoverageResult,
    val userPriority: Int
) : Input(data) {
    val coverage: Double
        get() = coverageResult.score()

    /** A minimization is possible if minimized input is successful, has the same coverage and produces the same result. */
    override fun minimize(coverageRunner: CoverageRunner, targetMethod: TargetMethod, dropBytesEnabled: Boolean) =
        if (userPriority < 0) {
            this
        } else {
            InputMinimizer<ExecutedInput>(coverageRunner, targetMethod, dropBytesEnabled).minimize(this)
        }

    override fun mutate(mutator: InputMutator) = mutator.mutate(this)

    override fun save(storage: Storage): Input = this.also {
        if (userPriority > 0) {
            storage.save(this)
        }
    }

    override fun hashCode() = coverageResult.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is ExecutedInput) return false
        return userPriority == other.userPriority && coverageResult == other.coverageResult
    }
}
