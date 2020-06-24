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
    open fun priority() = Double.MAX_VALUE

    fun run(coverageRunner: CoverageRunner, targetMethod: TargetMethod) =
        InputRunner.executeInput(coverageRunner, targetMethod, this)

    open fun mutate(mutator: InputMutator) = this
    open fun minimize(coverageRunner: CoverageRunner, targetMethod: TargetMethod) = this
    open fun save(storage: Storage, force: Boolean = false) = this
}
