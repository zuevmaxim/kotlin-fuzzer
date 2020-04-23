package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

internal class InsertBytesMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        val index = if (bytes.isEmpty()) 0 else Random.nextInt(bytes.size + 1)
        val length = Random.nextInt(MAX_SIZE) + 1
        val newBytes = ByteArray(bytes.size + length)
        bytes.copyInto(newBytes, startIndex = 0, endIndex = index)
        Random.nextBytes(newBytes, fromIndex = index, toIndex = index + length)
        bytes.copyInto(newBytes, destinationOffset = index + length, startIndex = index)
        return newBytes
    }

    companion object {
        private const val MAX_SIZE = 2
    }
}
