package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

class AddByteMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        val list = bytes.toMutableList()
        list.add(Random.nextBytes(1)[0])
        return list.toByteArray()
    }

}
