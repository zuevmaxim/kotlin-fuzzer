package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

/**
 * Minimizes executed input.
 * Checks that minimized input has the same return value and coverage.
 */
class ExecutedInputMinimizer(private val storage: Storage) : InputHandler<ExecutedInput>() {

    override fun run(input: ExecutedInput) {
        val executedInput = InputMinimizer<ExecutedInput>(storage.methodRunner, storage.targetMethod).minimize(input) { newInput ->
            when (newInput) {
                is ExecutedInput -> newInput.userPriority == input.userPriority && newInput.coverageResult == input.coverageResult
                else -> false
            }
        }
        onResult(executedInput)
    }
}
