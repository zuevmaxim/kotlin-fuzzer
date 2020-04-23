package ru.example.kotlinfuzzer.fuzzing.mutation

import java.nio.ByteBuffer
import kotlin.random.Random

internal abstract class ReplaceNumberMutation<T> : Mutation {
    /** Size of number in bytes. */
    abstract val numberSize: Int

    /**
     * Mutate number from buffer.
     * @param buffer contains 'numberSize' bytes, buffer is in read mode.
     * @return buffer with 'numberSize' bytes that present mutated number, buffer is in read mode
     */
    abstract fun mutateNumber(buffer: ByteBuffer): ByteBuffer

    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.size < numberSize) {
            return bytes
        }
        val index = Random.nextInt(bytes.size - numberSize + 1)
        val buffer = ByteBuffer.allocate(numberSize).put(bytes, index, numberSize).flip()
        val newBuffer = mutateNumber(buffer)
        return bytes.clone().also { newBytes ->
            newBuffer.get(newBytes, index, numberSize)
        }
    }
}
