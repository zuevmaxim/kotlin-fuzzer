package ru.example.kotlinfuzzer.fuzzing.input

import ru.example.kotlinfuzzer.fuzzing.inputhandlers.InputMutator
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.InputRunner
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

open class Input(val data: ByteArray) : ByteArrayHash(data) {
    open fun priority() = Int.MAX_VALUE

    fun run(runner: InputRunner): Input {
        var result = this
        runner.run(this, { result = it }, { result = it })
        return result
    }

    open fun mutate(mutator: InputMutator) = this
    open fun minimize(storage: Storage) = this
    open fun save(storage: Storage) = this
}
