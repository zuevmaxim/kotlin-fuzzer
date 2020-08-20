package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.Input
import kotlinx.fuzzer.fuzzing.mutation.MutationFactory
import kotlinx.fuzzer.fuzzing.storage.Storage

/**
 * Mutates input and submits new tasks.
 * Number of mutations is proportional to ratio of input coverage to the best current coverage.
 */
class InputMutator(
    private val fuzzer: Fuzzer,
    private val storage: Storage,
    private val context: FuzzerContext,
    private val mutationNumber: Int = DEFAULT_MUTATION_COUNT
) {
    private val factory = MutationFactory(storage)

    /**
     * Mutate best corpus input.
     * @return [input]
     */
    fun mutate(input: ExecutedInput): ExecutedInput {
        val bestCoverage = storage.bestCoverage.get()
        val k = input.coverageResult.otherCoverageRatio(bestCoverage)
        factory.mutate(input.data, (k * mutationNumber).toInt())
            .map { Input(it) }
            .map { InputTask(context, it) }
            .forEach { fuzzer.submit(it) }
        return input
    }
}

/** Default mutation count equals 1 allows to control linear growth of task queue, */
private const val DEFAULT_MUTATION_COUNT = 1
