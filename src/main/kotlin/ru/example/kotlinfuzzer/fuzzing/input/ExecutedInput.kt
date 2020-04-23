package ru.example.kotlinfuzzer.fuzzing.input

import ru.example.kotlinfuzzer.coverage.CoverageResult
import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.TargetMethod
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.InputMinimizer
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.InputMutator
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

class ExecutedInput(
    data: ByteArray,
    private val executionTimeMs: Long,
    val coverageResult: CoverageResult,
    val userPriority: Int
) : Input(data) {
    override fun priority(): Int {
        // TODO use execution time, user priority, length?
        return coverageResult.percent().toInt()
    }

    override fun minimize(methodRunner: MethodRunner, targetMethod: TargetMethod) = if (userPriority < 0) this
    else InputMinimizer<ExecutedInput>(methodRunner, targetMethod).minimize(this) { newInput ->
        when (newInput) {
            is ExecutedInput -> newInput.userPriority == this.userPriority && newInput.coverageResult == this.coverageResult
            else -> false
        }
    }

    override fun mutate(mutator: InputMutator) = this.also {
        mutator.mutate(this)
    }

    override fun save(storage: Storage) = this.also {
        if (userPriority > 0) {
            storage.save(this)
        }
    }
}
