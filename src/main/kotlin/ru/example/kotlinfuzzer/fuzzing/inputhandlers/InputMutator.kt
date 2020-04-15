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
        val maxPriority = fuzzer.maxPriority.get()
        val k = if (maxPriority == 0) 1.0 else input.priority().toDouble() / maxPriority
        List((k * mutationNumber).toInt()) { MutationFactory.mutate(input.data) }
            .map { Input(it) }
            .map { InputTask(handlers, it) }
            .forEach { fuzzer.submit(it) }
    }
}
