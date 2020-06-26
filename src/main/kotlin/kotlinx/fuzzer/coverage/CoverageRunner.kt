package kotlinx.fuzzer.coverage

import kotlinx.fuzzer.coverage.jacoco.JacocoCoverageRunner

/** Defines the way of running test with coverage. In particular, resolves class loading. */
interface CoverageRunner {
    fun runWithCoverage(f: () -> Unit): CoverageResult
    fun loadClass(name: String): Class<*>?
}

/** Create concrete CoverageRunner. */
fun createCoverageRunner(classpath: List<String>, packages: Collection<String>): CoverageRunner {
    return JacocoCoverageRunner(classpath, packages)
}
