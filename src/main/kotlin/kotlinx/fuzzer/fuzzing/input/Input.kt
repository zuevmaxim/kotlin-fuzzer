package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMutator
import kotlinx.fuzzer.fuzzing.inputhandlers.InputRunner
import kotlinx.fuzzer.fuzzing.storage.Storage

/**
 * Fuzzer execution task.
 * [data] - byte representation of input.
 */
open class Input(val data: ByteArray) {

    fun run(coverageRunner: CoverageRunner, targetMethod: TargetMethod, preconditions: Collection<Input> = emptyList()) =
        InputRunner.executeInput(coverageRunner, targetMethod, this, preconditions)

    open fun mutate(mutator: InputMutator) = this
    open fun minimize(coverageRunner: CoverageRunner, targetMethod: TargetMethod) = this
    open fun save(storage: Storage) = this
}
