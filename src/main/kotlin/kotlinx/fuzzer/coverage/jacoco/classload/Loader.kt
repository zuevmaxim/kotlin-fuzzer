package kotlinx.fuzzer.coverage.jacoco.classload

import com.google.common.reflect.ClassPath
import org.jacoco.core.instr.Instrumenter
import org.jacoco.core.runtime.LoggerRuntime
import java.io.File
import java.net.URLClassLoader

/**
 * Prepares code in classpath for loading and specified packages for instrumenting.
 */
@Suppress("UnstableApiUsage")
internal class Loader(
    classpath: Collection<String>,
    private val instrumentedPackages: Collection<String>
) {
    private val urlClassLoader = URLClassLoader(pathsToUrls(classpath).toTypedArray())
    private val memoryClassLoader = MemoryClassLoader(urlClassLoader)

    /** This class loader should be used for loading all classes from classpath. */
    val classLoader: ClassLoader = memoryClassLoader

    /** Loads classes from instrumented packages with coverage. */
    fun load(runtime: LoggerRuntime): Collection<ByteArray> {
        val classPath = ClassPath.from(urlClassLoader)
        return classPath
            .allClasses
            .filter { shouldBeCovered(it.name) }
            .associate { it.name to it.asByteSource().read() }
            .onEach { transform(it.key, it.value, runtime) }
            .map { it.value }
            .also { check(it.isNotEmpty()) { "Expected non empty package." } }
    }

    private fun shouldBeCovered(className: String): Boolean = instrumentedPackages.any { className.isInPackage(it) }

    private fun String.isInPackage(packageName: String): Boolean {
        val name = this.replace('/', '.')
        return name.startsWith(packageName)
                && (name.length == packageName.length || name[packageName.length] == '.')
    }

    private fun pathsToUrls(paths: Collection<String>) = paths.map { File(it).toURI().toURL() }

    /**
     * Transforms class bytes to run code with coverage.
     * Adds class definition to classloader.
     */
    private fun transform(
        className: String,
        classBytes: ByteArray,
        runtime: LoggerRuntime
    ) {
        val instrumenter = Instrumenter(runtime)
        val instrumented = instrumenter.instrument(classBytes, javaClass.name)

        memoryClassLoader.addDefinition(className, instrumented)
    }
}
