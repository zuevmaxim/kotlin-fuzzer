package kotlinx.fuzzer.fuzzing.mutation

import kotlin.random.Random

/** Replace ascii digit with random digit. */
internal class ReplaceDigitMutation : Mutation {
    override fun mutate(bytes: ByteArray): ByteArray? {
        val digitIndexes = bytes.indices.filter { bytes[it].isDigit() }
        if (digitIndexes.isEmpty()) {
            return null
        }
        val index = digitIndexes[Random.nextInt(digitIndexes.size)]
        return bytes.clone().also { newBytes ->
            newBytes[index] = Random.nextInt('0'.toInt(), '9'.toInt()).toByte()
        }
    }
}

internal fun Byte.isDigit() = '0'.toByte() <= this && this <= '9'.toByte()
