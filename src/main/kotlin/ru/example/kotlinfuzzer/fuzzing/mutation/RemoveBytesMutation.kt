package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

class RemoveBytesMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.isEmpty()) {
            return bytes
        }
        val index = Random.nextInt(bytes.size)
        val length = Random.nextInt(bytes.size - index)
        val newBytes = ByteArray(bytes.size - length)
        bytes.copyInto(newBytes, startIndex = 0, endIndex = index)
        bytes.copyInto(newBytes, destinationOffset = index, startIndex = index + length)
        return newBytes
    }

}
