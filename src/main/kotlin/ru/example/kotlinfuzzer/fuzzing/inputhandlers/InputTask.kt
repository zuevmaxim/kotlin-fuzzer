package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.input.Input
import ru.example.kotlinfuzzer.fuzzing.storage.ContextFactory

class InputTask(
    private val contextFactory: ContextFactory,
    private val input: Input
) : Runnable {
    override fun run() {
        val context = contextFactory.acquire()
        val targetMethod = context.targetMethod
        val methodRunner = context.methodRunner
        input
            .run(methodRunner, targetMethod)
            .mutate(context.mutator)
            .minimize(methodRunner, targetMethod)
            .save(context.storage)
    }
}
