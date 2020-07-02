package kotlinx.fuzzer.fuzzing.mutation

import kotlin.random.Random

/** Replace byte with random letter from 'a'..'z'. */
internal class ReplaceCharMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray? {
        if (bytes.isEmpty()) {
            return null
        }
        val newBytes = bytes.clone()
        val index = Random.nextInt(bytes.size)
        newBytes[index] = Random.nextInt('a'.toInt(), 'z'.toInt()).toByte()
        return newBytes
    }

}
