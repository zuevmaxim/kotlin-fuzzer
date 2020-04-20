package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.input.Input

class InputTask(
    private val handlers: Handlers,
    private val input: Input
) : Runnable {
    override fun run() {
        input
            .run(handlers.runner)
            //.mutate(handlers.mutator)
            .minimize(handlers.storage)
            .save(handlers.storage)
    }
}
