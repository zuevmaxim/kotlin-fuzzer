package ru.example.kotlinfuzzer.fuzzing.input

open class Input(val data: ByteArray) : ByteArrayHash(data) {
    open fun priority() = Int.MAX_VALUE
}
