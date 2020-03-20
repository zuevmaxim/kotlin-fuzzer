package ru.example.kotlinfuzzer

import org.jacoco.core.instr.Instrumenter
import org.jacoco.core.runtime.LoggerRuntime


object CodeCoverageClassTransformer {
    fun transform(targetName: String, classBytes: ByteArray, runtime: LoggerRuntime): Class<*> {
        val instrumenter = Instrumenter(runtime)
        val instrumented = instrumenter.instrument(classBytes, targetName)

        val memoryClassLoader = MemoryClassLoader()
        memoryClassLoader.addDefinition(targetName, instrumented)
        return memoryClassLoader.loadClass(targetName)
    }
}
