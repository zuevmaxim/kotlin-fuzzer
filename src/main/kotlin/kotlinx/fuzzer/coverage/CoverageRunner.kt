package kotlinx.fuzzer.coverage

import kotlinx.fuzzer.coverage.jwp.JwpCoverageRunner

/** Defines the way of running test with coverage. In particular, resolves class loading. */
interface CoverageRunner {
    fun runWithCoverage(f: () -> Unit): CoverageResult
    fun loadClass(name: String): Class<*>?
}

private var coverageRunner: CoverageRunner? = null

/** Create concrete CoverageRunner. */
@Synchronized
fun createCoverageRunner(classpath: List<String>, packages: Collection<String>): CoverageRunner {
    if (coverageRunner == null) {
        coverageRunner = JwpCoverageRunner(classpath, PackagesToCover(packages))
    }
    return coverageRunner!!
}
