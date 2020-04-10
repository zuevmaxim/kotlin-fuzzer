package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.input.Input
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

class HandlersNet(fuzzer: Fuzzer, storage: Storage) {
    private val runner = InputRunner(storage)
    private val executedMinimizer = ExecutedInputMinimizer(storage)
    private val failMinimizer = FailInputMinimizer(storage)
    private val executedSaver = ExecutedInputSaver(storage)
    private val failSaver = FailInputSaver(storage)
    private val mutator = InputMutator(fuzzer, this)

    init {
        runner.nextHandler(FailInput::class.java, failMinimizer)
        failMinimizer.nextHandler(FailInput::class.java, failSaver)

        runner.nextHandler(ExecutedInput::class.java, mutator)
        mutator.nextHandler(ExecutedInput::class.java, executedMinimizer)
        executedMinimizer.nextHandler(ExecutedInput::class.java, executedSaver)
    }

    fun run(input: Input) = runner.run(input)
}
