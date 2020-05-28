package kotlinx.fuzzer.tests.apache.zip

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ApacheZipTest {
    private fun compress(type: String, input: ByteArray, entry: Class<*>, name: String): File {
        val compressed = File.createTempFile("apache_", ".$type")
        val fos = FileOutputStream(compressed)
        ArchiveStreamFactory().createArchiveOutputStream(type, fos).use { aos ->
            val entryInstance = entry.getConstructor(String::class.java).newInstance(name) as ArchiveEntry
            aos.putArchiveEntry(entryInstance)
            aos.write(input)
            aos.closeArchiveEntry()
        }
        return compressed
    }

    private fun decompress(type: String, compressed: File, entry: Class<*>): Pair<ByteArray, String> {
        val fis = FileInputStream(compressed)
        ArchiveStreamFactory().createArchiveInputStream(type, fis).also { ais ->
            val zae = ais.nextEntry ?: throw IOException("Null entry")
            entry.cast(zae)
            return ais.readAllBytes() to zae.name
        }
    }

    fun test(bytes: ByteArray): Int {
        val files = mutableListOf<File>()
        fun exit() = files.forEach { it.delete() }
        val compressed = File.createTempFile("input_apache_", ".zip").also { files.add(it) }
        FileOutputStream(compressed).use { fos -> fos.write(bytes) }
        val (source, name) = try {
            decompress("zip", compressed, ZipArchiveEntry::class.java)
        } catch (e: IOException) {
            exit()
            return 0
        }
        val compressed2 = compress("zip", source, ZipArchiveEntry::class.java, name).also { files.add(it) }
        val (result, name2) = decompress("zip", compressed2, ZipArchiveEntry::class.java)
        check(name == name2) { "Expected name $name, but $name2 found" }
        check(source.contentEquals(result)) { "Content differs." }
        exit()
        return 2
    }

}
