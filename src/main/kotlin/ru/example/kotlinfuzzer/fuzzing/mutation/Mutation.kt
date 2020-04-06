package ru.example.kotlinfuzzer.fuzzing.mutation

interface Mutation {
    fun mutate(bytes: ByteArray): ByteArray
}
