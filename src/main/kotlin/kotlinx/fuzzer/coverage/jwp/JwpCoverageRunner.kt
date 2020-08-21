package kotlinx.fuzzer.coverage.jwp

import kotlinx.fuzzer.coverage.CoverageResult
import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.coverage.PackagesToCover
import java.io.File
import java.net.URLClassLoader

/** Code coverage using manual instrumentation. Usage of this runner is thread safe. */
internal class JwpCoverageRunner(classpath: List<String>, packages: PackagesToCover) : CoverageRunner {
    private val urlClassLoader = URLClassLoader(pathsToUrls(classpath).toTypedArray())
    private val tracer = InstrumentingTracer()

    init {
        transform(packages)
    }

    private fun singleRunWithCoverage(f: () -> Unit): CoverageResult {
        tracer.startTrace()
        val result = runCatching(f)
        val branchHashes = tracer.stopTrace()
        result.onFailure { throw it }
        checkNotNull(branchHashes) { "Instrumenting isn't available." }
        return JwpCoverageResult(branchHashes)
    }

    /**
     * Measure code coverage while [f] running.
     * Runs [f] until no class transformation appears in order to deal with static initializers
     * as their execution spoils reproducibility of [f] execution.
     */
    override fun runWithCoverage(f: () -> Unit): CoverageResult {
        var result: CoverageResult
        do {
            classLoaded.remove()
            result = singleRunWithCoverage(f)
        } while (classLoaded.get())
        return result
    }

    override fun loadClass(name: String): Class<*>? = urlClassLoader.loadClass(name)

    private fun pathsToUrls(paths: Collection<String>) = paths.map { File(it).toURI().toURL() }
}
