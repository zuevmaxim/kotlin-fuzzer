package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Input
import kotlinx.fuzzer.fuzzing.storage.ContextFactory
import kotlinx.fuzzer.fuzzing.storage.Storage

class InputTask(
    private val contextFactory: ContextFactory,
    private val input: Input
) : Runnable {

    override fun run() {
        val context = contextFactory.context()
        val targetMethod = context.targetMethod
        val methodRunner = context.coverageRunner
        val compositeCoverageCount = context.compositeCoverageCount
        val preconditions =
            if (context.storage.corpusInputs.size == 0) emptyList()
            else generateSequence { context.storage.corpusInputs.random() }.take(compositeCoverageCount).toList()
        input
            .run(methodRunner, targetMethod, preconditions)
            .mutate(context.mutator)
            .minimize(methodRunner, targetMethod, context.storage)
            .save(context.storage)
    }

    private fun Input.minimize(coverageRunner: CoverageRunner, targetMethod: TargetMethod, storage: Storage): Input {
        val isCorpusExecutedInput = this is ExecutedInput && storage.isBestInput(this)
        val isFailInput = this is FailInput
        val shouldMinimize = isCorpusExecutedInput || isFailInput
        return if (!shouldMinimize) this else minimize(coverageRunner, targetMethod)
    }
}
