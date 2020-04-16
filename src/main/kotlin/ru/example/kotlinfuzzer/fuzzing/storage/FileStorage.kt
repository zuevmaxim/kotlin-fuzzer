package ru.example.kotlinfuzzer.fuzzing.storage

import ru.example.kotlinfuzzer.fuzzing.input.ExecutedInput
import ru.example.kotlinfuzzer.fuzzing.input.FailInput
import ru.example.kotlinfuzzer.fuzzing.input.Hash
import java.io.File
import java.io.PrintWriter


/** Saves and loads inputs as files. */
class FileStorage(workingDirectory: File, name: String) {
    private val directory = File(workingDirectory, name).also {
        it.mkdirs()
    }

    fun save(input: ExecutedInput) = saveInput(input.data, input.hash)

    fun save(input: FailInput) {
        saveInput(input.data, input.hash)
        val file = File(directory, input.hash.toString() + ".error")
        file.createNewFile()
        PrintWriter(file).use { out -> input.e.printStackTrace(out) }
        val dataFile = File(directory, input.hash.toString() + ".txt")
        dataFile.createNewFile()
        PrintWriter(dataFile).use { out -> out.print(input.data.joinToString(", ", prefix = "[", postfix = "]") { String.format("0x%02x", it) }) }
    }

    internal fun saveInput(data: ByteArray, hash: Hash) {
        val file = File(directory, hash.toString())
        file.createNewFile()
        file.writeBytes(data)
    }

    fun listFilesContent() = directory.listFiles()?.map { it.readBytes() }

    fun containsFile(hash: Hash) = File(directory, hash.toString()).exists()
}
