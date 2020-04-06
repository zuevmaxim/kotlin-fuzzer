package ru.example.kotlinfuzzer.fuzzing.input

class FailInput(val data: ByteArray, val e: Throwable) : ByteArrayHash(data)
