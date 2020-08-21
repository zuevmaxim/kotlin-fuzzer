package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMinimizer
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMutator
import kotlinx.fuzzer.fuzzing.inputhandlers.InputRunner
import kotlinx.fuzzer.fuzzing.storage.Storage

/**
 * Fuzzer execution task.
 * [data] - byte representation of input.
 */
open class Input(val data: ByteArray) {

    /**
     * Execute this input.
     * @param preconditions list of other inputs to run before this one. It is used in corpus minimization.
     */
    fun run(
        coverageRunner: CoverageRunner,
        targetMethod: TargetMethod,
        preconditions: Collection<Input> = emptyList()
    ) = InputRunner.executeInput(coverageRunner, targetMethod, this, preconditions)

    /** Use this input to create new inputs with some mutation. */
    open fun mutate(mutator: InputMutator) = this

    /** Input minimization tries to cut input while it's execution does not change. */
    open fun minimize(minimizer: InputMinimizer) = this

    /** Save input into [storage]. */
    open fun save(storage: Storage) = this
}
