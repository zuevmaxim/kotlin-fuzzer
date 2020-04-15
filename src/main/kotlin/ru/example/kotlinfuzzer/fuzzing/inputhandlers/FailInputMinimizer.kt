package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

/**
 * Minimizes failed input.
 * Checks that minimized input still fails with same exception.
 */
class FailInputMinimizer(private val storage: Storage) : InputHandler<FailInput>() {

    override fun run(input: FailInput) {
        val failInput = InputMinimizer<FailInput>(storage.methodRunner, storage.targetMethod).minimize(input) { newInput ->
            when (newInput) {
                is FailInput -> input.e::class == newInput.e::class
                else -> false
            }
        }
        onResult(failInput)
    }
}
