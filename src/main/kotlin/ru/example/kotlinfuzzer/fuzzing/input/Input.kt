package ru.example.kotlinfuzzer.fuzzing.input

class Input(val data: ByteArray) : ByteArrayHash(data) {
    fun priority() = Int.MAX_VALUE
}
