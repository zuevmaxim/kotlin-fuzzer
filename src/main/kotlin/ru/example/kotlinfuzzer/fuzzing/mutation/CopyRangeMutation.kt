package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

internal class CopyRangeMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.size < 2) {
            return bytes
        }
        val index = Random.nextInt(bytes.size - 1)
        val length = Random.nextInt((bytes.size - index) / 2) + 1
        val newBytes = bytes.clone()
        bytes.copyInto(newBytes, destinationOffset = index + length, startIndex = index, endIndex = index + length)
        return newBytes
    }
}
