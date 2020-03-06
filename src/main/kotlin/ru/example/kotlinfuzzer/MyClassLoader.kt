package ru.example.kotlinfuzzer

import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions

class MyClassLoader(path: String, className: String) {
    private val clazz: KClass<*>?

    init {
        clazz = loadClass(path, className)
    }

    fun getMethods(methodName: String): List<KFunction<*>>? = clazz?.functions?.filter { it.name == methodName }

    fun createInstance(): Any? = InstanceCreator.create(clazz)

    private fun loadClass(path: String, className: String): KClass<*>? {
        val url = File(path).toURI().toURL()
        URLClassLoader(arrayOf(url)).use { loader ->
            return loader.loadClass(className).kotlin
        }
    }

}
