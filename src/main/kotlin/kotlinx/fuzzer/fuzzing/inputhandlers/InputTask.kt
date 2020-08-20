package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.fuzzing.input.Input

class InputTask(
    private val context: FuzzerContext,
    private val input: Input
) : Runnable {

    override fun run() {
        input
            .run(context.coverageRunner, context.targetMethod)
            .mutate(context.mutator)
            .save(context.storage)
    }
}
