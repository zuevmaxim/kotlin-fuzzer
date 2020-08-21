package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.Input

/**
 * Tries to minimize input. Uses [minimizeArray] method.
 */
class InputMinimizer<T : Input>(private val coverageRunner: CoverageRunner, private val targetMethod: TargetMethod) {

    /**
     * Minimize input.
     * A minimization is possible if minimized input equals the original one.
     * @param input immutable input to minimize
     * @return new minimized input or the same one if no minimization is possible
     */
    fun minimize(input: T): T {
        var bestInput = input
        minimizeArray(input.data) { candidate ->
            val executionResult = InputRunner.executeInput(coverageRunner, targetMethod, Input(candidate))
            if (executionResult == bestInput) {
                if (executionResult.data === bestInput.data) return@minimizeArray true
                @Suppress("UNCHECKED_CAST")
                bestInput = executionResult as T // suppose that equals returns false if type differs
                true
            } else {
                false
            }
        }
        return bestInput
    }

}
