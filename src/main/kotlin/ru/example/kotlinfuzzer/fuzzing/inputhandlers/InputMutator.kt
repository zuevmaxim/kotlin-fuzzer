package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.Input
import ru.example.kotlinfuzzer.fuzzing.mutation.MutationFactory
import ru.example.kotlinfuzzer.fuzzing.storage.ContextFactory
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

/** Mutates input and submits new tasks. */
class InputMutator(
    private val fuzzer: Fuzzer,
    private val storage: Storage,
    private val contextFactory: ContextFactory,
    private val mutationNumber: Int = 5
) {

    fun mutate(input: ExecutedInput) {
        val bestCoverage = storage.bestCoverage.get()
        val k = input.coverageResult.ratio(bestCoverage)
        MutationFactory.mutate(input.data, (k * mutationNumber).toInt())
            .map { Input(it) }
            .map { InputTask(contextFactory, it) }
            .forEach { fuzzer.submit(it) }
    }
}
