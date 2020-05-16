package ru.example.kotlinfuzzer.fuzzing.input

open class ByteArrayHash(bytes: ByteArray) {
    val hash by lazy { Hash(bytes) }
}
