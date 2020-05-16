package kotlinx.fuzzer.fuzzing.mutation

import kotlin.random.Random

/** Insert a range of random letters from 'a'..'z'. */
internal class InsertCharsMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray {
        val index = if (bytes.isEmpty()) 0 else Random.nextInt(bytes.size + 1)
        val length = Random.nextInt(MAX_INSERT_RANGE_SIZE) + 1
        val newBytes = ByteArray(bytes.size + length)
        bytes.copyInto(newBytes, startIndex = 0, endIndex = index)
        for (i in index until index + length) {
            newBytes[i] = Random.nextInt('a'.toInt(), 'z'.toInt()).toByte()
        }
        bytes.copyInto(newBytes, destinationOffset = index + length, startIndex = index)
        return newBytes
    }

}

private const val MAX_INSERT_RANGE_SIZE = 2
