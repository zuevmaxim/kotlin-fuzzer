package kotlinx.fuzzer.coverage.jwp

import kotlinx.fuzzer.coverage.CoverageResult

class JwpCoverageResult(private val branches: Array<BranchHit>) : CoverageResult {
    override fun score(): Double {
        return branches.size.toDouble()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is JwpCoverageResult) return false
        return score() == other.score()
    }

    override fun hashCode(): Int {
        return score().hashCode()
    }
}
