package ru.example.kotlinfuzzer

import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.SessionInfoStore
import org.jacoco.core.runtime.LoggerRuntime
import org.jacoco.core.runtime.RuntimeData

class MethodRunner(
    path: String,
    private val className: String
) {

    private val runtime = LoggerRuntime()
    private val data = RuntimeData()
    private val classes: Map<Class<*>, ByteArray>
    private val targetClass: Class<*>

    init {
        runtime.startup(data)
        classes = PackageCodeCoverageClassLoader.load(path, runtime)
        targetClass = classes.keys.find { it.name == className }!!
    }

    fun run(methodName: String): CoverageResult {
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
