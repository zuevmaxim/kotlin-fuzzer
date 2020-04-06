package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

class AddCharMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        val list = bytes.toMutableList()
        list.add(Random.nextInt('a'.toInt(), 'z'.toInt()).toByte())
        return list.toByteArray()
    }

}
