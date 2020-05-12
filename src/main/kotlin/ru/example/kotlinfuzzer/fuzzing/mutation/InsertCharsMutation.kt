package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

internal class InsertCharsMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        val index = if (bytes.isEmpty()) 0 else Random.nextInt(bytes.size + 1)
        val length = Random.nextInt(MAX_SIZE) + 1
        val newBytes = ByteArray(bytes.size + length)
        bytes.copyInto(newBytes, startIndex = 0, endIndex = index)
        for (i in index until index + length) {
            newBytes[i] = Random.nextInt('a'.toInt(), 'z'.toInt()).toByte()
        }
        bytes.copyInto(newBytes, destinationOffset = index + length, startIndex = index)
        return newBytes
    }

    companion object {
        private const val MAX_SIZE = 2
    }
}