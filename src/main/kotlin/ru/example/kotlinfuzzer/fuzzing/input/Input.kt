package ru.example.kotlinfuzzer.fuzzing.input

import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.TargetMethod
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.InputMutator
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.InputRunner
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

open class Input(val data: ByteArray) : ByteArrayHash(data) {
    open fun priority() = Double.MAX_VALUE

    fun run(methodRunner: MethodRunner, targetMethod: TargetMethod): Input {
        var result = this
        InputRunner.executeInput(methodRunner, targetMethod, this, { result = it }, { result = it })
        return result
    }

    open fun mutate(mutator: InputMutator) = this
    open fun minimize(methodRunner: MethodRunner, targetMethod: TargetMethod) = this
    open fun save(storage: Storage) = this
}
