package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.TargetMethod
import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.input.Input
import ru.example.kotlinfuzzer.fuzzing.storage.ContextFactory
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

open class InputTask(
    private val contextFactory: ContextFactory,
    private val input: Input
) : Runnable {
    override fun run() {
        val context = contextFactory.acquire()
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


    protected open val forceSave = false
}

class CorpusInputTask(
    contextFactory: ContextFactory,
    input: Input
) : InputTask(contextFactory, input) {
    override val forceSave = true
}
