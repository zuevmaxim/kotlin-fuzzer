package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

class ReplaceByteMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.isNotEmpty()) {
            val newBytes = bytes.clone()
            val index = Random.nextInt(bytes.size)
            newBytes[index] = Random.nextBytes(1)[0]
            return newBytes
        }
        return bytes
    }

}
