package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

/** Swap random bytes. */
internal class SwapBytesMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.size < 2) {
            return bytes
        }
        val index1 = Random.nextInt(bytes.size)
        var index2: Int
        do {
            index2 = Random.nextInt(bytes.size)
        } while (index2 == index1)

        return bytes.clone().also { newBytes ->
            val tmp = newBytes[index1]
            newBytes[index1] = newBytes[index2]
            newBytes[index2] = tmp
        }
    }

}
