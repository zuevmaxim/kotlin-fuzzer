package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.MethodRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMutator
import kotlinx.fuzzer.fuzzing.inputhandlers.InputRunner
import kotlinx.fuzzer.fuzzing.storage.LocalStorage

open class Input(val data: ByteArray) : ByteArrayHash(data) {
    open fun priority() = Double.MAX_VALUE

    fun run(methodRunner: MethodRunner, targetMethod: TargetMethod): Input {
        var result = this
        InputRunner.executeInput(methodRunner, targetMethod, this, { result = it }, { result = it })
        return result
    }

    open fun mutate(mutator: InputMutator) = this
    open fun minimize(methodRunner: MethodRunner, targetMethod: TargetMethod) = this
    open fun save(storage: LocalStorage, force: Boolean = false) = this
}
