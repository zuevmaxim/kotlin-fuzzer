package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.fuzzing.Fuzzer
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.Input
import kotlinx.fuzzer.fuzzing.mutation.MutationFactory
import kotlinx.fuzzer.fuzzing.storage.ContextFactory
import kotlinx.fuzzer.fuzzing.storage.Storage

/**
 * Mutates input and submits new tasks.
 * Number of mutations is proportional to ratio of input coverage to the best current coverage.
 */
class InputMutator(
    private val fuzzer: Fuzzer,
    private val storage: Storage,
    private val contextFactory: ContextFactory,
    private val mutationNumber: Int = 5
) {
    private val factory = MutationFactory(storage)

    /**
     * Mutate best corpus input.
     * @return [input]
     */
    fun mutate(input: ExecutedInput): ExecutedInput {
        val bestCoverage = storage.bestCoverage
        val k = input.coverageResult.otherCoverageRatio(bestCoverage)
        factory.mutate(input.data, (k * mutationNumber).toInt())
            .map { Input(it) }
            .map { InputTask(contextFactory, it) }
            .forEach { fuzzer.submit(it) }
        return input
    }
}
