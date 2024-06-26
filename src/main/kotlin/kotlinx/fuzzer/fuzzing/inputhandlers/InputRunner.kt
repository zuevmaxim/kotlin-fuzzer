package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Input

object InputRunner {
    /**
     * Execute [input].
     * @param preconditions list of other inputs to run before [input]. It is used in corpus minimization.
     * @return successful or fail input
     */
    fun executeInput(
        coverageRunner: CoverageRunner,
        targetMethod: TargetMethod,
        input: Input,
        preconditions: Collection<Input> = emptyList()
    ): Input {
        var result = Result.success(-1)
        val coverageResult = coverageRunner.runWithCoverage {
            for (precondition in preconditions) {
                targetMethod.execute(precondition)
            }
            result = targetMethod.execute(input)
        }

        var inputResult = input
        result.onFailure { exception ->
            inputResult = FailInput(input.data, exception.cause!!)
        }.onSuccess { returnValue ->
            inputResult = ExecutedInput(input.data, coverageResult, returnValue)
        }
        return inputResult
    }
}

