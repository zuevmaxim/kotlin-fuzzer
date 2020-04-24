package ru.example.kotlinfuzzer.fuzzing.mutation

import java.nio.ByteBuffer
import kotlin.random.Random


internal const val MAX_MUTATION_VALUE = 1
private fun numberMutation(): Int {
    var result: Int
    do {
        result = Random.nextInt(MAX_MUTATION_VALUE * 2 + 1) - MAX_MUTATION_VALUE
    } while (result == 0)
    return result
}

internal class AddSubtractByteMutation : ReplaceNumberMutation<Byte>() {
    override val numberSize = Byte.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val byte = (buffer.get() + numberMutation()).toByte()
        return buffer.clear().put(byte).flip()
    }
}

internal class AddSubtractCharMutation : ReplaceNumberMutation<Char>() {
    override val numberSize = Char.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val char = buffer.char + numberMutation()
        return buffer.clear().putChar(char).flip()
    }
}

internal class AddSubtractIntMutation : ReplaceNumberMutation<Int>() {
    override val numberSize = Int.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val int = buffer.int + numberMutation()
        return buffer.clear().putInt(int).flip()
    }
}

internal class AddSubtractLongMutation : ReplaceNumberMutation<Long>() {
    override val numberSize = Long.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val long = buffer.long + numberMutation()
        return buffer.clear().putLong(long).flip()
    }
}
