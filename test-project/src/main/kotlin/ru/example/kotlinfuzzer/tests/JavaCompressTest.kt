package ru.example.kotlinfuzzer.tests

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class JavaCompressTest {

    private fun unzip(input: ByteArray): ByteArray? {
        ZipInputStream(ByteArrayInputStream(input)).use { zis ->
            zis.nextEntry ?: return null
            return zis.readAllBytes().also {
                zis.closeEntry()
            }
        }
    }

    private fun zip(input: ByteArray): ByteArray {
        val output = ByteArrayOutputStream()
        ZipOutputStream(output).use { zos ->
            zos.putNextEntry(ZipEntry("name"))
            zos.write(input)
        }
        return output.toByteArray()
    }

    fun test(zipped: ByteArray): Int {
        val unzipped = try {
            unzip(zipped) ?: return 0
        } catch (e: ZipException) {
            return -1
        } catch (e: IOException) {
            return -1
        }
        val zipped2 = zip(unzipped)
        val unzipped2 = unzip(zipped2)
        checkNotNull(unzipped2) { "Successful unzip expected" }
        check(unzipped.contentEquals(unzipped2))
        return 1
    }
}
