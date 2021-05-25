package kotlinx.fuzzer.fuzzing.input

import java.security.MessageDigest

/** SHA-1 hash. */
class Hash(data: ByteArray) {
    private val hash = sha1(data)

    /** Hex representation of byte array. */
    override fun toString(): String {
        return hash.joinToString("") { String.format("%02x", it) }
    }

    private fun sha1(data: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-1")
        digest.update(data, 0, data.size)
        return digest.digest()
    }
}
