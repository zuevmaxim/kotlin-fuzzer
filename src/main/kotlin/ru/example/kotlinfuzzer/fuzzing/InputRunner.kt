package ru.example.kotlinfuzzer.fuzzing

import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.input.Input
import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import kotlin.system.measureTimeMillis

class InputRunner(
    private val fuzzer: Fuzzer,
    private val storage: Storage,
    private val input: Input
) : InputHandler {
    override fun priority() = input.priority()

    override fun run() {
        if (storage.isAlreadyExecuted(input)) return
        val targetMethod = storage.targetMethod
        val methodRunner = storage.methodRunner

        var result = Result.success(-1)
        var executionTime: Long = 0
        val coverageResult = methodRunner.run {
            executionTime = measureTimeMillis {
                targetMethod.execute(input) { result = it }
            }
        }

        if (result.isFailure) {
            storage.save(FailInput(input.data, result.exceptionOrNull()!!.cause!!))
            return
        }
        val returnValue = result.getOrNull()!!
        storage.markExecuted(input.hash)
        if (returnValue >= 0) {
            val executedInput = ExecutedInput(input.data, executionTime, coverageResult, returnValue)
            if (returnValue > 0) {
                storage.save(executedInput)
            }
            val task = InputMutator(fuzzer, storage, executedInput)
            fuzzer.submit(task)
        }
    }

}
