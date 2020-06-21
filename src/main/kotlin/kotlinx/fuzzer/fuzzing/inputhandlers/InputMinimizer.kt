package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.coverage.MethodRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.Input

/**
 * Tries to minimize input.
 * Applies minimization methods: [cutTail] removes bytes from tail of array,
 * [dropBytes] tries to remove each byte of input (now is unused as it changes input significantly).
 */
class InputMinimizer<T : Input>(private val methodRunner: MethodRunner, private val targetMethod: TargetMethod) {

    private lateinit var bestInput: T

    /**
     * Minimize input.
     * A minimization is possible if minimized input has the same properties as the original(resolved by [isSame]).
     * @param input immutable input to minimize
     * @param isSame predicate that checks that minimized input has the same performance(namely the same type)
     * @return new minimized input or the same one if no minimization is possible
     */
    fun minimize(input: T, isSame: (Input) -> Boolean): T {
        bestInput = input
        val data = input.data.toList()
        cutTail(data, isSame)
        return bestInput
    }

    private fun roundUpToPowerOfTwo(x: Int): Int {
        var p = 1
        while (x > p) {
            p *= 2
        }
        return p
    }

    /** Produces log2(size of [list]) input executions. */
    private fun cutTail(list: List<Byte>, isSame: (Input) -> Boolean): List<Byte> {
        var data = list
        var n = roundUpToPowerOfTwo(list.size)

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
            while (n > 0 && n <= data.size) {
                val candidate = data.dropLast(n).toByteArray()
                InputRunner.executeInput(methodRunner, targetMethod, Input(candidate), cutIfSame, cutIfSame)
            }
            n /= 2
        }

        return data
    }

    /** Tries to delete every byte of input. Produces size of [list] input executions. */
    // TODO enable this mutation with option. It is useful when input in structureless.
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
