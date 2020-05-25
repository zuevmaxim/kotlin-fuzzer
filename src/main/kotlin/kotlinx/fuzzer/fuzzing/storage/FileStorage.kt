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

    fun save(input: ExecutedInput) = saveInput(input.data, input.hash)

    fun save(input: FailInput): Boolean {
        if (!saveInput(input.data, input.hash)) return false
        val file = File(directory, "${input.hash}.txt").apply { createNewFile() }
        file.printWriter().use { out ->
            input.e.printStackTrace(out)
            out.println(input.data.joinToString(", ", prefix = "[", postfix = "]") { String.format("0x%02x", it) })
        }
        return true
    }

    fun listFilesContent() = directory.listFiles()?.map { it.readBytes() }

    internal fun saveInput(data: ByteArray, hash: Hash): Boolean {
        val file = File(directory, hash.toString())
        if (file.createNewFile()) {
            file.writeBytes(data)
            return true
        }
        return false
    }
}
