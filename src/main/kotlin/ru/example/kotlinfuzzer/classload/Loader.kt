package ru.example.kotlinfuzzer.classload

import com.google.common.reflect.ClassPath
import org.jacoco.core.runtime.LoggerRuntime
import java.io.File
import java.net.URLClassLoader

class Loader(
    classpath: List<String>,
    private val instrumentedPackages: List<String>
) {
    private val urlClassLoader = URLClassLoader(pathsToUrls(classpath).toTypedArray())
    private val memoryClassLoader = MemoryClassLoader(urlClassLoader)

    fun classLoader(): ClassLoader = memoryClassLoader

    fun load(runtime: LoggerRuntime): Map<Class<*>, ByteArray> {
        val classPath = ClassPath.from(urlClassLoader)
        return instrumentedPackages
            .flatMap { classPath.getTopLevelClassesRecursive(it) }
            .distinct()
            .map { it.asByteSource().read() }
            .associateBy { CodeCoverageClassTransformer.transform(it, runtime, memoryClassLoader) }
    }

    companion object {
        private fun pathsToUrls(paths: List<String>) = paths.map { File(it).toURI().toURL() }
    }
}
