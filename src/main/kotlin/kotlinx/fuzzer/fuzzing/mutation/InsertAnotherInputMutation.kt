package kotlinx.fuzzer.fuzzing.mutation

import kotlinx.fuzzer.fuzzing.storage.Storage
import kotlin.random.Random

/** Insert a random range from random corpus input into original input. */
class InsertAnotherInputMutation(private val storage: Storage) : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray? {
        if (storage.corpusInputs.size < 2) {
            return null
        }
        val other = storage.corpusInputs.random().data
        if (other.size < MIN_INSERT_RANGE_LENGTH || other === bytes) {
            return null
        }

        val index = Random.nextInt(bytes.size + 1)
        val otherIndex = Random.nextInt(other.size - MIN_INSERT_RANGE_LENGTH + 1)
        val length = Random.nextInt(other.size - otherIndex - MIN_INSERT_RANGE_LENGTH + 1) + MIN_INSERT_RANGE_LENGTH
        if (bytes.isEmpty() && other.size == length) {
            return null
        }
        return ByteArray(bytes.size + length).also { newBytes ->
            bytes.copyInto(newBytes, startIndex = 0, endIndex = index)
            other.copyInto(newBytes, destinationOffset = index, startIndex = otherIndex, endIndex = otherIndex + length)
            bytes.copyInto(newBytes, destinationOffset = index + length, startIndex = index)
        }
    }

}

private const val MIN_INSERT_RANGE_LENGTH = 2
