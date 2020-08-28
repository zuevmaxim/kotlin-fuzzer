package kotlinx.fuzzer.coverage.jacoco

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.coverage.PackagesToCover
import kotlinx.fuzzer.coverage.jacoco.classload.Loader
import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.analysis.IClassCoverage
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.SessionInfoStore
import org.jacoco.core.runtime.LoggerRuntime
import org.jacoco.core.runtime.RuntimeData

/**
 * Code coverage using Jacoco library.
 * Uses class loader for bytecode transformation.
 * Usage of this runner is *not* thread safe.
 */
private class JacocoCoverageRunner(classpath: List<String>, packages: PackagesToCover) : CoverageRunner {
    private val loader = Loader(classpath, packages)
    private val runtime = LoggerRuntime()
    private val classes = loader.load(runtime)
    private val data = RuntimeData()

    init {
        runtime.startup(data)
    }

    /** Run function [f] with coverage of predefined packages. */
    override fun runWithCoverage(f: () -> Unit): JacocoCoverageResult {
        f()

        val executionData = ExecutionDataStore()
        val sessionInfos = SessionInfoStore()
        data.collect(executionData, sessionInfos, false)

        val coverageBuilder = CoverageBuilder()
        val analyzer = Analyzer(executionData, coverageBuilder)
        for (bytes in classes) {
            analyzer.analyzeClass(bytes, "JacocoCoverageRunner")
        }

        data.reset()
        return JacocoCoverageResult.sum(coverageBuilder.classes
            .map { it.toCoverageResult() })
    }

    override fun loadClass(name: String): Class<*>? = loader.classLoader.loadClass(name)
}

private fun IClassCoverage.toCoverageResult() = JacocoCoverageResult(
    methodCounter.totalCount, methodCounter.missedCount,
    lineCounter.totalCount, lineCounter.missedCount,
    branchCounter.totalCount, branchCounter.missedCount
)

/** Thread safe wrapper for [JacocoCoverageRunner]. */
internal class ThreadLocalJacocoCoverageRunner(classpath: List<String>, packages: PackagesToCover) : CoverageRunner {
    private val runner = ThreadLocal.withInitial { JacocoCoverageRunner(classpath, packages) }
    override fun runWithCoverage(f: () -> Unit) = runner.get().runWithCoverage(f)
    override fun loadClass(name: String) = runner.get().loadClass(name)
}
