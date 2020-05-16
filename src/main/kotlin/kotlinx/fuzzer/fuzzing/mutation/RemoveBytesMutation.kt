package kotlinx.fuzzer.fuzzing.mutation

import kotlin.math.min
import kotlin.random.Random

/** Cut a random range of bytes. */
internal class RemoveBytesMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        if (bytes.isEmpty()) {
            return bytes
        }
        val index = Random.nextInt(bytes.size)
        val length = min(Random.nextInt(bytes.size - index), MAX_DELETE_RANGE_SIZE)
        val newBytes = ByteArray(bytes.size - length)
        bytes.copyInto(newBytes, startIndex = 0, endIndex = index)
        bytes.copyInto(newBytes, destinationOffset = index, startIndex = index + length)
        return newBytes
    }

}

private const val MAX_DELETE_RANGE_SIZE = 5
