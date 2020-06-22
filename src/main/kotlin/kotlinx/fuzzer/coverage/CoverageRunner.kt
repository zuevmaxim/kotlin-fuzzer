package kotlinx.fuzzer.coverage

/** Defines the way of running test with coverage. In particular, resolves class loading. */
interface CoverageRunner {
    fun runWithCoverage(f: () -> Unit): CoverageResult
    fun loadClass(name: String): Class<*>?
}
