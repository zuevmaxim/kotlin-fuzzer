package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Input

object InputRunner {
    fun executeInput(
        coverageRunner: CoverageRunner,
        targetMethod: TargetMethod,
        input: Input,
        onSuccess: (ExecutedInput) -> Unit = {},
        onFail: (FailInput) -> Unit = {}
    ) {
        var result = Result.success(-1)
        val coverageResult = coverageRunner.runWithCoverage {
            targetMethod.execute(input) { result = it }
        }

        result.onFailure { exception ->
            val fail = FailInput(input.data, exception.cause!!)
            onFail(fail)
        }.onSuccess { returnValue ->
            val executedInput = ExecutedInput(input.data, coverageResult, returnValue)
            onSuccess(executedInput)
        }
    }
}

