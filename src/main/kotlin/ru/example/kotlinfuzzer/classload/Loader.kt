package ru.example.kotlinfuzzer.classload

import com.google.common.reflect.ClassPath
import org.jacoco.core.runtime.LoggerRuntime
import java.io.File
import java.net.URLClassLoader

/**
 * Prepares code in classpath for loading and specified packages for instrumenting.
 */
class Loader(
    classpath: List<String>,
    private val instrumentedPackages: List<String>
) {
    private val urlClassLoader = URLClassLoader(pathsToUrls(classpath).toTypedArray())
    private val memoryClassLoader = MemoryClassLoader(urlClassLoader)

    /** This class loader should be used for loading all classes from classpath. */
    fun classLoader(): ClassLoader = memoryClassLoader

    /** Loads classes from instrumented packages with coverage. */
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
