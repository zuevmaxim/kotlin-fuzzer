package ru.example.kotlinfuzzer.classload

import org.jacoco.core.instr.Instrumenter
import org.jacoco.core.runtime.LoggerRuntime


object CodeCoverageClassTransformer {
    /**
     * Transforms class bytes to run code with coverage.
     * @return instrumented class
     */
    fun transform(
        classBytes: ByteArray,
        runtime: LoggerRuntime,
        memoryClassLoader: MemoryClassLoader
    ): Class<*> {
        val instrumenter = Instrumenter(runtime)
        val instrumented = instrumenter.instrument(classBytes, javaClass.name)

        val hash = classBytes.hashCode().toString()
        memoryClassLoader.addDefinition(hash, instrumented)
        return memoryClassLoader.loadClass(hash)
    }
}
