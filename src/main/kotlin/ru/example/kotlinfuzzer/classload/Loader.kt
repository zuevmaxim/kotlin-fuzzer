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
    fun load(runtime: LoggerRuntime): Collection<ByteArray> {
        val classPath = ClassPath.from(urlClassLoader)
        return classPath
            .allClasses
            .filter { classInfo -> instrumentedPackages.any { packageName -> classInfo.isInPackage(packageName) } }
            .associate { Pair(it.name, it.asByteSource().read()) }
            .onEach { CodeCoverageClassTransformer.transform(it.key, it.value, runtime, memoryClassLoader) }
            .map { it.value }
            .also { check(it.isNotEmpty()) { "Expected non empty package." } }
    }

    private fun ClassPath.ClassInfo.isInPackage(packageName: String) =
        this.packageName.startsWith(packageName)
                && (this.packageName.length == packageName.length || this.packageName[packageName.length] == '.')

    companion object {
        private fun pathsToUrls(paths: List<String>) = paths.map { File(it).toURI().toURL() }
    }
}
