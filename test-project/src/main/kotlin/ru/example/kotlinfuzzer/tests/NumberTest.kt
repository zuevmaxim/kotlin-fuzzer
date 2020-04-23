package ru.example.kotlinfuzzer.tests

import java.nio.ByteBuffer

class NumberTest {
    fun divisionByZero(bytes: ByteArray): Int {
        if (bytes.size < 8) {
            return -1
        }
        val buffer = ByteBuffer.wrap(bytes)
        val x = buffer.int
        val y = buffer.int
        x / y
        return 1
    }
}
