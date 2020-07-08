package kotlinx.fuzzer.coverage

import kotlinx.fuzzer.coverage.jwp.JwpCoverageRunner

/** Defines the way of running test with coverage. In particular, resolves class loading. */
interface CoverageRunner {
    fun runWithCoverage(f: () -> Unit): CoverageResult
    fun loadClass(name: String): Class<*>?
}

/** Create concrete CoverageRunner. */
@Synchronized
fun createCoverageRunner(classpath: List<String>, packages: Collection<String>): CoverageRunner {
    return JwpCoverageRunner(classpath, PackagesToCover(packages))
}
