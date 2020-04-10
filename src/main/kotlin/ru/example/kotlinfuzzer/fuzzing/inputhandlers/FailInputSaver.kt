package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

class FailInputSaver(private val storage: Storage) : InputHandler<FailInput>() {
    override fun run(input: FailInput) {
        storage.save(input)
        onResult(input)
    }
}
