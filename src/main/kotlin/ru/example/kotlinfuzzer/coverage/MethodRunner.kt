package ru.example.kotlinfuzzer.coverage

import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.SessionInfoStore
import org.jacoco.core.runtime.LoggerRuntime
import org.jacoco.core.runtime.RuntimeData

class MethodRunner(
    getClasses: (LoggerRuntime) -> Map<Class<*>, ByteArray>
) {
    private val runtime = LoggerRuntime()
    private val classes = getClasses(runtime)
    private val data = RuntimeData()

    init {
        runtime.startup(data)
    }

    /** Run specified function with coverage. */
    fun run(f: () -> Unit): CoverageResult {
        f()

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
