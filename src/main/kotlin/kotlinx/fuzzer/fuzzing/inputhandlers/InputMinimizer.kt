package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.input.Input

/**
 * Tries to minimize input.
 * Applies minimization methods: [cutTail] removes bytes from tail of array,
 * [dropBytes] tries to remove each byte of input (now is unused as it changes input significantly).
 */
class InputMinimizer<T : Input>(
    private val coverageRunner: CoverageRunner,
    private val targetMethod: TargetMethod,
    private val dropBytesEnabled: Boolean
) {

    /** Minimized input. It must be equal to original input. */
    private lateinit var bestInput: T

    /**
     * Minimize input.
     * A minimization is possible if minimized input equals the original one.
     * @param input immutable input to minimize
     * @return new minimized input or the same one if no minimization is possible
     */
    fun minimize(input: T): T {
        bestInput = input
        val data = input.data.toList()
        val cutInput = cutTail(data)
        if (dropBytesEnabled) {
            dropBytes(cutInput)
        }
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
    private fun cutTail(list: List<Byte>): List<Byte> {
        var data = list
        var n = roundUpToPowerOfTwo(list.size)

        fun cutIfSame(input: Input) {
            if (input == bestInput) {
                data = data.dropLast(n)
                @Suppress("UNCHECKED_CAST") // suppose that equals returns false if type differs
                bestInput = input as T
            } else {
                n /= 2
            }
        }

        while (n > 0) {
            while (n > 0 && n <= data.size) {
                val candidate = data.dropLast(n).toByteArray()
                val result = InputRunner.executeInput(coverageRunner, targetMethod, Input(candidate))
                cutIfSame(result)
            }
            n /= 2
        }

        return data
    }

    /** Tries to delete every byte of input. Produces size of [list] input executions. */
    private fun dropBytes(list: List<Byte>): List<Byte> {
        val data = list.toMutableList()
        var i = 0

        fun dropIfSame(byte: Byte, input: Input) {
            if (input == bestInput) {
                @Suppress("UNCHECKED_CAST") // suppose that equals returns false if type differs
                bestInput = input as T
            } else {
                data.add(i, byte)
                i++
            }
        }

        while (i < data.size) {
            val byte = data.removeAt(i)
            val result = InputRunner.executeInput(coverageRunner, targetMethod, Input(data.toByteArray()))
            dropIfSame(byte, result)
        }

        return data
    }
}
