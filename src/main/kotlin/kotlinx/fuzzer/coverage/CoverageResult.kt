package kotlinx.fuzzer.coverage

/** Coverage of executed code. */
interface CoverageResult : Comparable<CoverageResult> {

    fun score(): Double

    /** Ratio with other coverage result. Deals with zero score of other result. */
    fun otherCoverageRatio(other: CoverageResult): Double {
        val otherScore = other.score()
        return if (otherScore == 0.0) {
            1.0
        } else {
            score() / other.score()
        }
    }

    override fun compareTo(other: CoverageResult) = score().compareTo(other.score())

    companion object {
        /** Default coverage score is zero. */
        val default = object : CoverageResult {
            override fun score() = 0.0
        }
    }
}
