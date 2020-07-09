package kotlinx.fuzzer.coverage.jwp

import kotlinx.fuzzer.coverage.CoverageResult
import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.coverage.PackagesToCover
import java.io.File
import java.net.URLClassLoader

internal class JwpCoverageRunner(classpath: List<String>, packages: PackagesToCover) : CoverageRunner {
    private val urlClassLoader = URLClassLoader(pathsToUrls(classpath).toTypedArray())
    private val tracer = Tracer.Instrumenting()

    init {
        transform(packages)
    }

    override fun runWithCoverage(f: () -> Unit): CoverageResult {
        tracer.startTrace()
        val result = runCatching(f)
        val branchHashes = tracer.stopTrace()
        result.onFailure { throw it }
        checkNotNull(branchHashes) { "Instrumenting isn't available." }
        return JwpCoverageResult(branchHashes)
    }

    override fun loadClass(name: String): Class<*>? = urlClassLoader.loadClass(name)

    private fun pathsToUrls(paths: Collection<String>) = paths.map { File(it).toURI().toURL() }
}
