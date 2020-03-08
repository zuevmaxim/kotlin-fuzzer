package ru.example.kotlinfuzzer

import java.io.File
import java.net.URLClassLoader

object MyClassLoader {

    fun loadClassBytes(path: String, className: String): ByteArray {
        val url = File(path).toURI().toURL()
        return URLClassLoader(arrayOf(url)).getResourceAsStream("${className.replace('.', '/')}.class").use {
            it!!.readAllBytes()
        }
    }

}
