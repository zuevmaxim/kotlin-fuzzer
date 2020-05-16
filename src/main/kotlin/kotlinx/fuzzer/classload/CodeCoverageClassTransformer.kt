package kotlinx.fuzzer.classload

import org.jacoco.core.instr.Instrumenter
import org.jacoco.core.runtime.LoggerRuntime


object CodeCoverageClassTransformer {
    /**
     * Transforms class bytes to run code with coverage.
     * Adds class definition to classloader.
     */
    fun transform(
        className: String,
        classBytes: ByteArray,
        runtime: LoggerRuntime,
        memoryClassLoader: MemoryClassLoader
    ) {
        val instrumenter = Instrumenter(runtime)
        val instrumented = instrumenter.instrument(classBytes, javaClass.name)

        memoryClassLoader.addDefinition(className, instrumented)
    }
}
