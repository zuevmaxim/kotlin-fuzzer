package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Hash
import java.io.File
import java.util.concurrent.atomic.AtomicInteger


/** Saves and loads inputs as files. */
class FileStorage(workingDirectory: File, name: String) {
    private val directory = File(workingDirectory, name).also {
        it.mkdirs()
    }
    private val count = AtomicInteger(0)

    fun count() = count.get()

    fun save(input: ExecutedInput) = saveInput(input.data)

    fun save(input: FailInput, hash: Hash) {
        saveInput(input.data, hash)
        val file = File(directory, "$hash.txt").apply { createNewFile() }
        file.printWriter().use { out ->
            input.e.printStackTrace(out)
            out.println(input.data.joinToString(", ", prefix = "[", postfix = "]") { String.format("0x%02x", it) })
        }
    }

    fun listFilesContent() = directory.listFiles()?.map { it.readBytes() }

    internal fun saveInput(data: ByteArray, hash: Hash = Hash(data)): File {
        val file = File(directory, hash.toString())
        if (file.createNewFile()) {
            count.incrementAndGet()
        }
        file.writeBytes(data)
        return file
    }
}
