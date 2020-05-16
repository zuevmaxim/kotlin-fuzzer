package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.CoverageResult
import kotlinx.fuzzer.coverage.MethodRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMinimizer
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMutator
import kotlinx.fuzzer.fuzzing.storage.Storage

class ExecutedInput(
    data: ByteArray,
    private val executionTimeMs: Long,
    val coverageResult: CoverageResult,
    val userPriority: Int
) : Input(data) {
    override fun priority(): Double {
        // TODO use execution time, user priority, length?
        return coverageResult.percent()
    }

    override fun minimize(methodRunner: MethodRunner, targetMethod: TargetMethod) = if (userPriority < 0) this
    else InputMinimizer<ExecutedInput>(methodRunner, targetMethod).minimize(this) { newInput ->
        when (newInput) {
            is ExecutedInput -> newInput.userPriority == this.userPriority && newInput.coverageResult == this.coverageResult
            else -> false
        }
    }

    override fun mutate(mutator: InputMutator) = mutator.mutate(this)

    override fun save(storage: Storage, force: Boolean): Input = this.also {
        if (userPriority > 0) {
            storage.save(this, force)
        }
    }
}
