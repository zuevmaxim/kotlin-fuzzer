package ru.example.kotlinfuzzer

import java.io.File
import java.net.URLClassLoader

object BytesClassLoader {

    fun loadClassBytes(path: String, classFileName: String): ByteArray {
        val url = File(path).toURI().toURL()
        return URLClassLoader(arrayOf(url)).getResourceAsStream(classFileName).use {
            it!!.readAllBytes()
        }
    }
}
