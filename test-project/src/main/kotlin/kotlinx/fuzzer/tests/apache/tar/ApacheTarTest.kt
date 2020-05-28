package kotlinx.fuzzer.tests.apache.tar

import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ApacheTarTest {
    private fun compress(inputs: List<Pair<ByteArray, String>>): File {
        val compressed = File.createTempFile("apache_", ".tar")
        val fos = FileOutputStream(compressed)
        ArchiveStreamFactory().createArchiveOutputStream("tar", fos).use { aos ->
            for ((data, name) in inputs) {
                val entry = TarArchiveEntry(name)
                entry.size = data.size.toLong()
                aos.putArchiveEntry(entry)
                try {
                    aos.write(data)
                } finally {
                    aos.closeArchiveEntry()
                }
            }
        }
        return compressed
    }

    private fun decompress(compressed: File): List<Pair<ByteArray, String>> {
        val fis = FileInputStream(compressed)
        ArchiveStreamFactory().createArchiveInputStream("tar", fis).use { ais ->
            val result = mutableListOf<Pair<ByteArray, String>>()
            var zae = ais.nextEntry as TarArchiveEntry?
            while (zae != null) {
                val data = ais.readAllBytes()
                check(zae.size == data.size.toLong()) { "Incorrect size." }
                result.add(data to zae.name)
                zae = ais.nextEntry as TarArchiveEntry?
            }
            return result
        }
    }

    fun test(bytes: ByteArray): Int {
        val files = mutableListOf<File>()
        fun exit() = files.forEach { it.delete() }
        val compressed = File.createTempFile("input_apache_", ".tar").also { files.add(it) }
        FileOutputStream(compressed).use { fos -> fos.write(bytes) }
        val sources = try {
            decompress(compressed)
        } catch (e: IOException) {
            exit()
            return 0
        }
        val compressed2 = compress(sources).also { files.add(it) }
        val sources2 = decompress(compressed2)
        check(sources2.size == sources.size) { "Size equality expected." }
        for (i in sources.indices) {
            check(sources[i].second == sources2[i].second) { "Name equality expected." }
            check(sources[i].first.contentEquals(sources2[i].first)) { "Data equality expected." }
        }
        exit()
        return 2
    }

}
