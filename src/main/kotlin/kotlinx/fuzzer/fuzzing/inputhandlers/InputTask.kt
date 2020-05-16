package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.coverage.MethodRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Input
import kotlinx.fuzzer.fuzzing.storage.ContextFactory
import kotlinx.fuzzer.fuzzing.storage.Storage

open class InputTask(
    private val contextFactory: ContextFactory,
    private val input: Input
) : Runnable {
    /** Force save flag shows that this input should be saved anyway without any changes(minimization). */
    protected open val forceSave = false

    override fun run() {
        val context = contextFactory.context()
        val targetMethod = context.targetMethod
        val methodRunner = context.methodRunner
        input
            .run(methodRunner, targetMethod)
            .also { if (it is ExecutedInput) context.storage.executed.save(it) }
            .mutate(context.mutator)
            .minimize(methodRunner, targetMethod, context.storage, forceSave)
            .save(context.storage, forceSave)
    }

    private fun Input.minimize(methodRunner: MethodRunner, targetMethod: TargetMethod, storage: Storage, forceSave: Boolean): Input {
        val isCorpusExecutedInput = this is ExecutedInput && storage.isBestInput(this)
        val isFailInput = this is FailInput
        val shouldMinimize = !forceSave && (isCorpusExecutedInput || isFailInput)
        return if (!shouldMinimize) this else minimize(methodRunner, targetMethod)
    }
}

class CorpusInputTask(
    contextFactory: ContextFactory,
    input: Input
) : InputTask(contextFactory, input) {
    override val forceSave = true
}
