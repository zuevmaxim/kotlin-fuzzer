package kotlinx.fuzzer.fuzzing.mutation

import kotlin.random.Random

/**
 * Take a random range A and overwrite next range B with a copy of A.
 * Length of the mutated input equals original input length.
 */
internal class CopyRangeMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.size < 2) {
            return bytes
        }
        val index = Random.nextInt(bytes.size - 1)
        val length = Random.nextInt((bytes.size - index) / 2) + 1
        val newBytes = bytes.clone()
        bytes.copyInto(newBytes, destinationOffset = index + length, startIndex = index, endIndex = index + length)
        return newBytes
    }
}
