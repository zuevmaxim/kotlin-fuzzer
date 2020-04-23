package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

internal class ReplaceCharMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.isEmpty()) {
            return bytes
        }
        val newBytes = bytes.clone()
        val index = Random.nextInt(bytes.size)
        newBytes[index] = Random.nextInt('a'.toInt(), 'z'.toInt()).toByte()
        return newBytes
    }

}
