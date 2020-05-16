package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.TargetMethod
import ru.example.kotlinfuzzer.fuzzing.input.Input

/**
 * Tries to minimize input.
 * Applies minimization methods: [cutTail] removes bytes from tail of array,
 * [dropBytes] tries to remove each byte of input (now is unused as it changes input significantly).
 * A minimization is possible if minimized input has the same properties as the original.
 */
class InputMinimizer<T : Input>(private val methodRunner: MethodRunner, private val targetMethod: TargetMethod) {

    private lateinit var bestInput: T

    /**
     * Minimize input.
     * @param input immutable input to minimize
     * @param isSame predicate that checks that minimized input has the same performance
     * @return new minimized input or the same one if no minimization is possible
     */
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

    private fun dropBytes(list: List<Byte>, isSame: (Input) -> Boolean): List<Byte> {
        val data = list.toMutableList()
        var i = 0

        fun dropIfSame(byte: Byte): (Input) -> Unit = { input: Input ->
            if (isSame(input)) {
                @Suppress("UNCHECKED_CAST") // suppose that isSame returns false if type differs
                bestInput = input as T
            } else {
                data.add(i, byte)
                i++
            }
        }

        while (i < data.size) {
            val byte = data.removeAt(i)
            InputRunner.executeInput(methodRunner, targetMethod, Input(data.toByteArray()), dropIfSame(byte), dropIfSame(byte))
        }

        return data
    }
}
