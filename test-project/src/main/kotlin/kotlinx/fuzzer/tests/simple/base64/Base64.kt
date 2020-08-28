package kotlinx.fuzzer.tests.simple.base64

import java.util.*
import kotlin.collections.ArrayList

private const val BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
private const val BASE64_MASK: Byte = 0x3f
private const val BASE64_PAD = '='

private val BASE64_INVERSE_ALPHABET = IntArray(256) {
    BASE64_ALPHABET.indexOf(it.toChar())
}

private fun Int.toBase64(): Char = BASE64_ALPHABET[this]


object NativeBase64Encoder {
    private fun ByteArray.getOrZero(index: Int): Int = if (index >= size) 0 else get(index).toInt()

    fun encode(src: ByteArray): ByteArray {

        val result = ArrayList<Byte>(4 * src.size / 3)
        var index = 0
        while (index < src.size) {
            val symbolsLeft = src.size - index
            val padSize = if (symbolsLeft >= 3) 0 else (3 - symbolsLeft) * 8 / 6
            val chunk = (src.getOrZero(index) shl 16) or (src.getOrZero(index + 1) shl 8) or src.getOrZero(index + 2)
            index += 3

            for (i in 3 downTo padSize) {
                val char = (chunk shr (6 * i)) and BASE64_MASK.toInt()
                result.add(char.toBase64().toByte())
            }

            repeat(padSize) { result.add(BASE64_PAD.toByte()) }
        }

        return result.toByteArray()
    }

}

class Base64Test {
    fun test(bytes: ByteArray): Int {
        val encoded = NativeBase64Encoder.encode(bytes)
        val expected = Base64.getEncoder().encode(bytes)
        val decoded = Base64.getDecoder().decode(encoded)
        check(expected!!.contentEquals(encoded))
        { "Expected equality with java method: expected ${String(expected)} but ${String(encoded)} found." }
        check(decoded!!.contentEquals(bytes)) { "Expected same array." }
        return 1
    }
}
