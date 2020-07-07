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
        val thread = Thread.currentThread()
        tracer.startTrace(thread)
        f()
        val branches = tracer.stopTrace(thread)
        checkNotNull(branches) { "Instrumenting isn't available." }
        return JwpCoverageResult(branches)
    }

    override fun loadClass(name: String): Class<*>? = urlClassLoader.loadClass(name)

    private fun pathsToUrls(paths: Collection<String>) = paths.map { File(it).toURI().toURL() }
}
