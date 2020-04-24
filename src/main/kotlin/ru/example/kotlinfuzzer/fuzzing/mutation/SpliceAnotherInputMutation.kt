package ru.example.kotlinfuzzer.fuzzing.mutation

import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import java.lang.Integer.min
import kotlin.random.Random

class SpliceAnotherInputMutation(private val storage: Storage) : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (storage.corpusInputs.size < 2) {
            return bytes
        }
        val other = storage.corpusInputs.random().data
        if (other === bytes) {
            return bytes
        }
        val prefixLength = findCommonPrefix(bytes, other)
        val suffixLength = findCommonSuffix(bytes, other)
        val differenceLength = min(bytes.size, other.size) - prefixLength - suffixLength
        if (differenceLength < MIN_DIFFERENCE_LENGTH) {
            return bytes
        }
        val length = Random.nextInt(differenceLength - 2) + 1
        return bytes.clone().also { newBytes ->
            other.copyInto(newBytes, destinationOffset = prefixLength, startIndex = prefixLength, endIndex = prefixLength + length)
        }
    }

    private fun findCommonPrefix(a: ByteArray, b: ByteArray): Int {
        var index = 0
        while (index < a.size && index < b.size && a[index] == b[index]) {
            index++
        }
        return index
    }

    private fun findCommonSuffix(a: ByteArray, b: ByteArray): Int {
        var index = 0
        while (index < a.size && index < b.size && a[a.size - 1 - index] == b[b.size - 1 - index]) {
            index++
        }
        return index
    }

    companion object {
        private const val MIN_DIFFERENCE_LENGTH = 3
    }
}
