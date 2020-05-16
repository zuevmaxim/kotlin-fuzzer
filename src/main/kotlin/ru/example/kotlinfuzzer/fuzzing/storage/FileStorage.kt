package ru.example.kotlinfuzzer.fuzzing.storage

import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.input.Hash
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.util.concurrent.atomic.AtomicInteger


/** Saves and loads inputs as files. */
class FileStorage(workingDirectory: File, name: String) {
    private val directory = File(workingDirectory, name).also {
        it.mkdirs()
    }
    private val count = AtomicInteger(0)

    fun count() = count.get()

    fun save(input: ExecutedInput) = saveInput(input.data, input.hash)

    fun save(input: FailInput) {
        val file = saveInput(input.data, input.hash)
        PrintWriter(FileOutputStream(file, true)).use { out ->
            out.println("===END OF INPUT===")
            input.e.printStackTrace(out)
            out.println(input.data.joinToString(", ", prefix = "[", postfix = "]") { String.format("0x%02x", it) })
        }
    }

    fun listFilesContent() = directory.listFiles()?.map { it.readBytes() }

    internal fun saveInput(data: ByteArray, hash: Hash): File {
        val file = File(directory, hash.toString())
        if (file.createNewFile()) {
            count.incrementAndGet()
        }
        file.writeBytes(data)
        return file
    }
}
