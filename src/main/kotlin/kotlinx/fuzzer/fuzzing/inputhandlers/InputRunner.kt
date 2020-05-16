package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.coverage.MethodRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Input
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

        result.onFailure { exception ->
            val fail = FailInput(input.data, exception.cause!!)
            onFail(fail)
        }.onSuccess { returnValue ->
            val executedInput = ExecutedInput(input.data, executionTime, coverageResult, returnValue)
            onSuccess(executedInput)
        }
    }
}

