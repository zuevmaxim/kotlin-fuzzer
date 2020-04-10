package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.TargetMethod
import ru.example.kotlinfuzzer.fuzzing.input.Input

class InputMinimizer<T : Input>(private val methodRunner: MethodRunner, private val targetMethod: TargetMethod) {

    private lateinit var bestInput: T

    fun minimize(input: T, isSame: (Input) -> Boolean): T {
        bestInput = input
        val data = input.data.toList()
        cutTail(data, isSame)
        return bestInput
    }

    private fun cutTail(list: List<Byte>, isSame: (Input) -> Boolean): List<Byte> {
        var data = list
        var n = list.size / 2

        val cutIfSame = { input: Input ->
            if (isSame(input)) {
                data = data.dropLast(n)
                @Suppress("UNCHECKED_CAST") // suppose that isSame returns false if type differs
                bestInput = input as T
            } else {
                n /= 2
            }
        }

        while (n > 0) {
            while (n > 0 && n < data.size) {
                val candidate = data.dropLast(n).toByteArray()
                InputRunner.executeInput(methodRunner, targetMethod, Input(candidate), cutIfSame, cutIfSame)
            }
            n /= 2
        }

        return data
    }
}
