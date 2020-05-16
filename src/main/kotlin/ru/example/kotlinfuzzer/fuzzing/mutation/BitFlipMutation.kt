package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.experimental.xor
import kotlin.random.Random

/** Flip random bit. */
internal class BitFlipMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.isEmpty()) {
            return bytes
        }
        return bytes.clone().also { newBytes ->
            val index = Random.nextInt(newBytes.size)
            newBytes[index] = newBytes[index] xor (1 shl Random.nextInt(8)).toByte()
        }
    }
}
