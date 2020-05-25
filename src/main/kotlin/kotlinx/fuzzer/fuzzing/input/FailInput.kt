package kotlinx.fuzzer.fuzzing.input

import kotlinx.fuzzer.coverage.MethodRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.inputhandlers.InputMinimizer
import kotlinx.fuzzer.fuzzing.storage.LocalStorage

class FailInput(data: ByteArray, val e: Throwable) : Input(data) {
    override fun minimize(methodRunner: MethodRunner, targetMethod: TargetMethod) =
        InputMinimizer<FailInput>(methodRunner, targetMethod).minimize(this) { newInput ->
            when (newInput) {
                is FailInput -> this.e::class == newInput.e::class
                else -> false
            }
        }

    override fun save(storage: LocalStorage, force: Boolean): Input = this.also {
        storage.save(this)
    }
}
