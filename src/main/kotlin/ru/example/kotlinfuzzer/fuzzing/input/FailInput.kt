package ru.example.kotlinfuzzer.fuzzing.input

import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.TargetMethod
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.InputMinimizer
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

class FailInput(data: ByteArray, val e: Throwable) : Input(data) {
    override fun minimize(methodRunner: MethodRunner, targetMethod: TargetMethod) =
        InputMinimizer<FailInput>(methodRunner, targetMethod).minimize(this) { newInput ->
            when (newInput) {
                is FailInput -> this.e::class == newInput.e::class
                else -> false
            }
        }

    override fun save(storage: Storage, force: Boolean): Input = this.also {
        storage.save(this)
    }
}
