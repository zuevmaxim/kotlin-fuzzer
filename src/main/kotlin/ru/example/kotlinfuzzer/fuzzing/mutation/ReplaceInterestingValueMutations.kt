package ru.example.kotlinfuzzer.fuzzing.mutation

import java.nio.ByteBuffer

/** Replace byte with random interesting value. */
internal class ReplaceInterestingByteMutation : ReplaceNumberMutation<Byte>() {
    override val numberSize = Byte.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val byte = interestingValues.random()
        return buffer.clear().put(byte).flip()
    }

    companion object {
        /** Boundary Byte values. */
        val interestingValues = listOf<Byte>(0, 1, -1, 2, -2, 127, -128, 64, -64, 100, -100)
    }
}

/** Interpret 2 bytes as Char and replace it with random interesting value. */
internal class ReplaceInterestingCharMutation : ReplaceNumberMutation<Char>() {
    override val numberSize = Char.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val char = interestingValues.random()
        return buffer.clear().putChar(char).flip()
    }

    companion object {
        /** Boundary Char values. */
        val interestingValues = ReplaceInterestingByteMutation.interestingValues.map { it.toChar() }
            .plus(listOf(-32768, -129, 128, 255, 256, 512, 1000, 1024, 4096, 32767).map { it.toChar() })
    }
}

/** Interpret 4 bytes as Int and replace it with random interesting value. */
internal class ReplaceInterestingIntMutation : ReplaceNumberMutation<Int>() {
    override val numberSize = Int.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val int = interestingValues.random()
        return buffer.clear().putInt(int).flip()
    }

    companion object {
        /** Boundary Int values. */
        val interestingValues = ReplaceInterestingCharMutation.interestingValues.map { it.toInt() }
            .plus(listOf(-2147483648, -100663046, -32769, 32768, 65535, 65536, 100663045, 2147483647))
    }
}

/** Interpret 4 bytes as Long and replace it with random interesting value. */
internal class ReplaceInterestingLongMutation : ReplaceNumberMutation<Long>() {
    override val numberSize = Long.SIZE_BYTES
    override fun mutateNumber(buffer: ByteBuffer): ByteBuffer {
        val long = interestingValues.random()
        return buffer.clear().putLong(long).flip()
    }

    companion object {
        /** Boundary Long values. */
        val interestingValues = ReplaceInterestingIntMutation.interestingValues.map { it.toLong() }
            .plus(listOf(Long.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE / 2, Long.MIN_VALUE / 2))
    }
}
