package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

class ExecutedInputSaver(private val storage: Storage) : InputHandler<ExecutedInput>() {
    override fun run(input: ExecutedInput) {
        if (input.userPriority > 0) {
            storage.save(input)
        }
        onResult(input)
    }
}
