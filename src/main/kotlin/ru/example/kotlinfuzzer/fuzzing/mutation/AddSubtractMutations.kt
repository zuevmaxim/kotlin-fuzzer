package ru.example.kotlinfuzzer.fuzzing.mutation

import java.nio.ByteBuffer
import kotlin.random.Random


internal const val MAX_ADD_SUBTRACT_MUTATION_VALUE = 1
private fun nonZeroRandomNumber(): Int {
    var result: Int
    do {
        result = Random.nextInt(-MAX_ADD_SUBTRACT_MUTATION_VALUE, MAX_ADD_SUBTRACT_MUTATION_VALUE + 1)
    } while (result == 0)
    return result
}

/** Add/subtract a random number to/from byte. */
internal class AddSubtractByteMutation : ReplaceNumberMutation<Byte>() {
    override val numberSize = Byte.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val byte = (buffer.get() + nonZeroRandomNumber()).toByte()
        return buffer.clear().put(byte).flip()
    }
}

/** Interpret 2 bytes as Char and add/subtract a random number. */
internal class AddSubtractCharMutation : ReplaceNumberMutation<Char>() {
    override val numberSize = Char.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val char = buffer.char + nonZeroRandomNumber()
        return buffer.clear().putChar(char).flip()
    }
}

/** Interpret 4 bytes as Int and add/subtract a random number. */
internal class AddSubtractIntMutation : ReplaceNumberMutation<Int>() {
    override val numberSize = Int.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val int = buffer.int + nonZeroRandomNumber()
        return buffer.clear().putInt(int).flip()
    }
}

/** Interpret 8 bytes as Long and add/subtract a random number. */
internal class AddSubtractLongMutation : ReplaceNumberMutation<Long>() {
    override val numberSize = Long.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val long = buffer.long + nonZeroRandomNumber()
        return buffer.clear().putLong(long).flip()
    }
}
