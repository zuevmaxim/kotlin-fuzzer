package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.Input
import ru.example.kotlinfuzzer.fuzzing.mutation.MutationFactory
import ru.example.kotlinfuzzer.fuzzing.storage.ContextFactory
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

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
        val bestCoverage = storage.bestCoverage.get()
        val k = input.coverageResult.otherCoverageRatio(bestCoverage)
        factory.mutate(input.data, (k * mutationNumber).toInt())
            .map { Input(it) }
            .map { InputTask(contextFactory, it) }
            .forEach { fuzzer.submit(it) }
        return input
    }
}
