package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.input.Input

class InputTask(private val handlersNet: HandlersNet, private val input: Input) : Runnable {
    override fun run() {
        handlersNet.run(input)
    }
}
