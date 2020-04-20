package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.Input
import ru.example.kotlinfuzzer.fuzzing.mutation.MutationFactory

/** Mutates input and submits new tasks. */
class InputMutator(
    private val fuzzer: Fuzzer,
    private val handlers: Handlers,
    private val mutationNumber: Int = 5
) {

    fun mutate(input: ExecutedInput) {
        val bestCoverage = handlers.storage.bestCoverage.get()
        val k = input.coverageResult.ratio(bestCoverage)
        MutationFactory.mutate(input.data, (k * mutationNumber).toInt())
            .map { Input(it) }
            .map { InputTask(handlers, it) }
            .forEach { fuzzer.submit(it) }
    }
}
