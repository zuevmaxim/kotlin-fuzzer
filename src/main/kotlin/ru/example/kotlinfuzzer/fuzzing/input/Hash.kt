package ru.example.kotlinfuzzer.fuzzing.input

import java.security.MessageDigest


class Hash(data: ByteArray) {
    val hash = sha1(data)

    private fun sha1(data: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-1")
        digest.update(data, 0, data.size)
        return digest.digest()
    }

    override fun toString(): String {
        return hash.joinToString("") { String.format("%02x", it) }
    }
}
