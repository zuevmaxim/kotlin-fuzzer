package ru.example.kotlinfuzzer

import java.io.File
import java.lang.reflect.Method
import java.net.URLClassLoader

class MyClassLoader(path: String, className: String) {
    private val clazz: Class<*>?

    init {
        clazz = loadClass(path, className)
    }

    fun getMethod(methodName: String): Method? = clazz?.getMethod(methodName)

    fun createInstance(): Any? {
        val constructor = clazz?.getConstructor()
        return constructor?.newInstance()
    }

    private fun loadClass(path: String, className: String): Class<*>? {
        val url = File(path).toURI().toURL()
        URLClassLoader(arrayOf(url)).use { loader ->
            return loader.loadClass(className)
        }
    }

}
