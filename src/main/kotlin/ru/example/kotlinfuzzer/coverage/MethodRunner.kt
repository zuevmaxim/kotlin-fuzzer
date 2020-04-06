package ru.example.kotlinfuzzer.coverage

import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.SessionInfoStore
import org.jacoco.core.runtime.LoggerRuntime
import org.jacoco.core.runtime.RuntimeData

class MethodRunner(
    private val classLoader: ClassLoader,
    getClasses: (LoggerRuntime) -> Map<Class<*>, ByteArray>
) {
    private val runtime = LoggerRuntime()
    private val classes = getClasses(runtime)
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
}
