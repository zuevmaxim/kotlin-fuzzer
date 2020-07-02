package kotlinx.fuzzer.fuzzing.mutation

import kotlin.random.Random

/**
 * Take a random range and repeat it twice.
 * Length of the mutated input equals original input length plus length of the range.
 */
internal class DuplicateRangeMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray? {
        if (bytes.isEmpty()) {
            return null
        }
        val index = Random.nextInt(bytes.size)
        val length = Random.nextInt(bytes.size - index) + 1
        val newBytes = ByteArray(bytes.size + length)
        bytes.copyInto(newBytes, startIndex = 0, endIndex = index + length)
        bytes.copyInto(newBytes, destinationOffset = index + length, startIndex = index, endIndex = index + length)
        bytes.copyInto(newBytes, destinationOffset = index + 2 * length, startIndex = index + length)
        return newBytes
    }
}
