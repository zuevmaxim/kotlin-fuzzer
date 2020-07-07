package kotlinx.fuzzer.coverage

interface CoverageResult : Comparable<CoverageResult> {

    fun score(): Double

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
        val default = object : CoverageResult {
            override fun score() = 0.0
        }
    }
}
