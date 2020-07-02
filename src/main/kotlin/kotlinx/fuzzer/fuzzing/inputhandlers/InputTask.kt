package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.fuzzing.input.Input
import kotlinx.fuzzer.fuzzing.storage.ContextFactory

class InputTask(
    private val contextFactory: ContextFactory,
    private val input: Input
) : Runnable {

    override fun run() {
        val context = contextFactory.context()
        val preconditions = generatePreconditions(context)
        input
            .run(context.coverageRunner, context.targetMethod, preconditions)
            .mutate(context.mutator)
            .save(context.storage)
    }

    private fun generatePreconditions(context: FuzzerContext) = if (context.storage.corpusInputs.size == 0) {
        emptyList()
    } else {
        generateSequence { context.storage.corpusInputs.random() }
            .take(context.compositeCoverageCount)
            .toList()
    }
}
