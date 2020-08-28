package kotlinx.fuzzer.coverage.jwp

import kotlinx.fuzzer.coverage.CoverageResult

class JwpCoverageResult(private val branches: IntArray) : CoverageResult {
    private val hash = branches.contentHashCode()

    override fun score(): Double {
        return branches.size.toDouble()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is JwpCoverageResult) return false
        return branches.contentEquals(other.branches)
    }

    override fun hashCode() = hash
}
