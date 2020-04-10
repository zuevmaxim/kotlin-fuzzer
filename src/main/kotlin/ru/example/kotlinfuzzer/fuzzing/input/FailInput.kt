package ru.example.kotlinfuzzer.fuzzing.input

class FailInput(data: ByteArray, val e: Throwable) : Input(data)
