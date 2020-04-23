package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.TargetMethod
import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.input.Input
import kotlin.system.measureTimeMillis

object InputRunner {
    fun executeInput(
        methodRunner: MethodRunner,
        targetMethod: TargetMethod,
        input: Input,
        onSuccess: (ExecutedInput) -> Unit = {},
        onFail: (FailInput) -> Unit = {}
    ) {
        var result = Result.success(-1)
        var executionTime: Long = 0
        val coverageResult = methodRunner.run {
            executionTime = measureTimeMillis {
                targetMethod.execute(input) { result = it }
            }
        }

        if (result.isFailure) {
            val fail = FailInput(input.data, result.exceptionOrNull()!!.cause!!)
            onFail(fail)
            return
        }
        val returnValue = result.getOrNull()!!
        if (returnValue >= 0) {
            val executedInput = ExecutedInput(input.data, executionTime, coverageResult, returnValue)
            onSuccess(executedInput)
        }
    }
}

