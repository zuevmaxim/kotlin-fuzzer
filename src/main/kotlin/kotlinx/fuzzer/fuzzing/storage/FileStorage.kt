package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Hash
import java.io.File


/** Saves and loads inputs as files. */
class FileStorage(workingDirectory: File, name: String) {
    private val directory = File(workingDirectory, name).also {
        it.mkdirs()
    }

    fun save(input: ExecutedInput) = saveInput(input.data)

    /**
     * Creates two files: data file with content of input
     * and info file with exception stacktrace and hex input representation.
     */
    fun save(input: FailInput, hash: Hash): Boolean {
        if (!saveInput(input.data, hash)) return false
        val file = File(directory, "$hash.txt").apply { createNewFile() }
        file.printWriter().use { out ->
            input.e.printStackTrace(out)
            out.println(input.data.joinToString(", ", prefix = "[", postfix = "]") { String.format("0x%02x", it) })
        }
        return true
    }

    fun listFilesContent() = directory.listFiles()?.map { it.readBytes() }

    internal fun saveInput(data: ByteArray, hash: Hash = Hash(data)): Boolean {
        val file = File(directory, hash.toString())
        if (file.createNewFile()) {
            file.writeBytes(data)
            return true
        }
        return false
    }
}
