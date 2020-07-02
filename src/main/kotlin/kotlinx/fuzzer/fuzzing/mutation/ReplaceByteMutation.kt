package kotlinx.fuzzer.fuzzing.mutation

import kotlin.random.Random

/** Replace byte with random value. */
internal class ReplaceByteMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray? {
        if (bytes.isEmpty()) {
            return null
        }
        val newBytes = bytes.clone()
        val index = Random.nextInt(bytes.size)
        newBytes[index] = Random.nextBytes(1)[0]
        return newBytes
    }

}
