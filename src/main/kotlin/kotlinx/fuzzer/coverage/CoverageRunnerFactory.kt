package kotlinx.fuzzer.coverage

import kotlinx.fuzzer.coverage.jacoco.JacocoCoverageRunner

/** Create concrete CoverageRunner. */
object CoverageRunnerFactory {
    fun createCoverageRunner(classpath: List<String>, packages: Collection<String>): CoverageRunner {
        return JacocoCoverageRunner(classpath, packages)
    }
}
