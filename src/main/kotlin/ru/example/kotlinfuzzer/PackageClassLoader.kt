package ru.example.kotlinfuzzer

import org.jacoco.core.runtime.LoggerRuntime
import java.io.File

object PackageCodeCoverageClassLoader {
    fun load(path: String, runtime: LoggerRuntime): Map<Class<*>, ByteArray> {
        val memoryClassLoader = MemoryClassLoader()
        return File(path).walk()
            .filter { it.name.endsWith(".class") }
            .map { BytesClassLoader.loadClassBytes(it.parent, it.name) }
            .associateBy { CodeCoverageClassTransformer.transform(it, runtime, memoryClassLoader) }
    }
}
