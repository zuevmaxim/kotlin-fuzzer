package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

class DuplicateRangeMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.isEmpty()) {
            return bytes
        }
        val index = Random.nextInt(bytes.size)
        val length = Random.nextInt(bytes.size - index) + 1
        val newBytes = ByteArray(bytes.size + length)
        bytes.copyInto(newBytes, startIndex = 0, endIndex = index + length)
        bytes.copyInto(newBytes, destinationOffset = index + length, startIndex = index, endIndex = index + length)
        bytes.copyInto(newBytes, destinationOffset = index + 2 * length, startIndex = index + length)
        return newBytes
    }
}
