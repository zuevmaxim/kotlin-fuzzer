package ru.example.kotlinfuzzer

import java.io.File
import java.net.URLClassLoader

object BytesClassLoader {

    fun loadClassBytes(path: String, className: String): ByteArray {
        val url = File(path).toURI().toURL()
        return URLClassLoader(arrayOf(url)).getResourceAsStream(classFileName(className)).use {
            it!!.readAllBytes()
        }
    }

    private fun classFileName(className: String) = "${className.replace('.', '/')}.class"

}
