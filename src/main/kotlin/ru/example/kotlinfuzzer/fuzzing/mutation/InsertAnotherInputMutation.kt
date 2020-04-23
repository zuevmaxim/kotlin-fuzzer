package ru.example.kotlinfuzzer.fuzzing.mutation

import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import kotlin.random.Random

class InsertAnotherInputMutation(private val storage: Storage) : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (storage.corpusInputs.size < 2) {
            return bytes
        }
        val other = storage.corpusInputs.random().data
        if (other.size < MIN_INSERT_RANGE_LENGTH || other === bytes) {
            return bytes
        }

        val index = Random.nextInt(bytes.size + 1)
        val otherIndex = Random.nextInt(other.size - MIN_INSERT_RANGE_LENGTH + 1)
        val length = Random.nextInt(other.size - otherIndex - MIN_INSERT_RANGE_LENGTH + 1) + MIN_INSERT_RANGE_LENGTH
        if (bytes.isEmpty() && other.size == length) {
            return bytes
        }
        return ByteArray(bytes.size + length).also { newBytes ->
            bytes.copyInto(newBytes, startIndex = 0, endIndex = index)
            other.copyInto(newBytes, destinationOffset = index, startIndex = otherIndex, endIndex = otherIndex + length)
            bytes.copyInto(newBytes, destinationOffset = index + length, startIndex = index)
        }
    }

    companion object {
        private const val MIN_INSERT_RANGE_LENGTH = 2
    }
}
