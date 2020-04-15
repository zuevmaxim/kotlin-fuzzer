package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

class Handlers(val storage: Storage, fuzzer: Fuzzer, mutationNumber: Int = 5) {
    val runner = InputRunner(storage)
    val mutator = InputMutator(fuzzer, this, mutationNumber)
}
