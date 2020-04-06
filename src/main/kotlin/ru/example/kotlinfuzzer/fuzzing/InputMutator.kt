package ru.example.kotlinfuzzer.fuzzing

import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.Input
import ru.example.kotlinfuzzer.fuzzing.mutation.MutationFactory
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

class InputMutator(
    private val fuzzer: Fuzzer,
    private val storage: Storage,
    private val input: ExecutedInput
) : InputHandler {
    override fun priority() = input.priority()

    override fun run() {
        List(256) { MutationFactory.mutate(input.data) }
            .map { Input(it) }
            .map { InputRunner(fuzzer, storage, it) }
            .forEach { fuzzer.submit(it) }
    }
}
