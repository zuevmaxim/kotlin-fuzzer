package kotlinx.fuzzer.coverage

import kotlinx.fuzzer.coverage.jwp.JwpCoverageRunner

/** Defines the way of running test with coverage. In particular, resolves class loading. */
interface CoverageRunner {
    /** Measure code coverage while execution of [f]. */
    fun runWithCoverage(f: () -> Unit): CoverageResult
    fun loadClass(name: String): Class<*>?
}

/** Create concrete CoverageRunner. */
fun createCoverageRunner(classpath: List<String>, packages: Collection<String>): CoverageRunner {
    return JwpCoverageRunner(classpath, PackagesToCover(packages))
}
