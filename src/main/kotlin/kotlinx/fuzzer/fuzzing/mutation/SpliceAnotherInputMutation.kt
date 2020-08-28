package kotlinx.fuzzer.fuzzing.mutation

import kotlinx.fuzzer.fuzzing.storage.Storage
import java.lang.Integer.min
import kotlin.random.Random

/** Insert a random range from corpus input without common suffix and prefix. */
class SpliceAnotherInputMutation(private val storage: Storage) : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray? {
        if (storage.corpusInputs.size < 2) {
            return null
        }
        val other = storage.corpusInputs.next()?.data ?: return null
        if (other === bytes) {
            return null
        }
        val prefixLength = findCommonPrefix(bytes, other)
        val suffixLength = findCommonSuffix(bytes, other)
        val differenceLength = min(bytes.size, other.size) - prefixLength - suffixLength
        if (differenceLength < MIN_DIFFERENCE_RANGE_LENGTH) {
            return null
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

}

private const val MIN_DIFFERENCE_RANGE_LENGTH = 3
