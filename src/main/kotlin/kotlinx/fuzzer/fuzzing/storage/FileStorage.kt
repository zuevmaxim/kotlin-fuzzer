package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Hash
import kotlinx.fuzzer.fuzzing.log.fileAndLineNumber
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
    fun save(input: FailInput): Boolean {
        val (fileName, lineNumber) = input.e.fileAndLineNumber()
        var name = "${input.e::class.simpleName}(${fileName}-$lineNumber)"
        if (!saveInput(input.data, name)) {
            name = Hash(input.data).toString()
            if (!saveInput(input.data, name)) return false
        }
        val file = File(directory, "$name.txt").apply { createNewFile() }
        file.printWriter().use { out ->
            input.e.printStackTrace(out)
            out.println(input.data.joinToString(", ", prefix = "[", postfix = "]") { String.format("0x%02x", it) })
        }
        return true
    }

    fun listFilesContent() = directory.listFiles()?.map { it.readBytes() }

    internal fun saveInput(data: ByteArray, name: String = Hash(data).toString()): Boolean {
        val file = File(directory, name)
        if (file.createNewFile()) {
            file.writeBytes(data)
            return true
        }
        return false
    }
}
