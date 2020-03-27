package ru.example.kotlinfuzzer

import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.SessionInfoStore
import org.jacoco.core.runtime.LoggerRuntime
import org.jacoco.core.runtime.RuntimeData

class MethodRunner private constructor(
    private val runtime: LoggerRuntime,
    private val classes: Map<Class<*>, ByteArray>,
    private val classLoader: ClassLoader
) {
    private val data = RuntimeData()

    init {
        runtime.startup(data)
    }

    fun run(className: String, methodName: String): CoverageResult {
        val targetClass = classLoader.loadClass(className) ?: error("Class $className not found.")
        val targetInstance = InstanceCreator.create(targetClass)
        targetClass.declaredMethods.filter { it.name == methodName }.forEach {
            val parameters = InstanceCreator.createParameters(it.parameters)
            it.invoke(targetInstance, *parameters)
        }

        val executionData = ExecutionDataStore()
        val sessionInfos = SessionInfoStore()
        data.collect(executionData, sessionInfos, false)

        val coverageBuilder = CoverageBuilder()
        val analyzer = Analyzer(executionData, coverageBuilder)
        for (bytes in classes.values) {
            analyzer.analyzeClass(bytes, "MethodRunner")
        }

        data.reset()
        return CoverageResult.sum(coverageBuilder.classes)
    }

    fun shutdown() {
        runtime.shutdown()
    }


    class Builder {
        private val runtime = LoggerRuntime()
        private var classes: Map<Class<*>, ByteArray>? = null
        private var classLoader: ClassLoader? = null

        fun classes(getClasses: (LoggerRuntime) -> Map<Class<*>, ByteArray>): Builder {
            classes = getClasses(runtime)
            return this
        }

        fun classLoader(loader: ClassLoader): Builder {
            classLoader = loader
            return this
        }

        fun build(): MethodRunner {
            val classes = classes
            val loader = classLoader
            checkNotNull(classes) { "Classes are not initialized!" }
            checkNotNull(loader) { "Class loader is not initialized!" }
            return MethodRunner(runtime, classes, loader)
        }

    }
}
