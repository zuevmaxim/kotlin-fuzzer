package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

class RemoveBytesMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.isEmpty()) {
            return bytes
        }
        val list = bytes.toMutableList()
        val index = Random.nextInt(bytes.size)
        val length = Random.nextInt(bytes.size - index)
        list.subList(index, index + length).clear()
        return list.toByteArray()
    }

}
