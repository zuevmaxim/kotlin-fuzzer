package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.CoverageResult
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMinimizer
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMutator
import kotlinx.fuzzer.fuzzing.storage.Storage

/** Successfully executed input. */
class ExecutedInput(
    data: ByteArray,
    val coverageResult: CoverageResult,
    /** A value returned by user. */
    val userPriority: Int
) : Input(data) {
    val coverage: Double
        get() = coverageResult.score()

    /** A minimization is possible if minimized input is successful, has the same coverage and produces the same result. */
    override fun minimize(minimizer: InputMinimizer) = if (userPriority < 0) {
        this
    } else {
        minimizer.minimize(this)
    }

    override fun mutate(mutator: InputMutator) = mutator.mutate(this)

    override fun save(storage: Storage): Input = this.also {
        if (userPriority > 0) {
            storage.save(this)
        }
    }

    /** Coverage information hashcode. */
    override fun hashCode() = coverageResult.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is ExecutedInput) return false
        return userPriority == other.userPriority && coverageResult == other.coverageResult
    }
}
