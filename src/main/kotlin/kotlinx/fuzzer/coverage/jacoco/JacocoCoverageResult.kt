package kotlinx.fuzzer.coverage.jacoco

import kotlinx.fuzzer.coverage.CoverageResult

data class JacocoCoverageResult(
    val totalMethods: Int, val missedMethods: Int,
    val totalLines: Int, val missedLines: Int,
    val totalBranches: Int, val missedBranches: Int
) : CoverageResult {

    override fun score() = listOf(methodsPercent(), linesPercent(), branchesPercent()).average()

    operator fun compareTo(other: JacocoCoverageResult) = score().compareTo(other.score())

    override fun otherCoverageRatio(other: CoverageResult): Double {
        if (other !is JacocoCoverageResult) {
            return super.otherCoverageRatio(other)
        }
        return listOf(
            visitedMethods() to other.visitedMethods(),
            visitedLines() to other.visitedLines(),
            visitedBranches() to other.visitedBranches()
        )
            .filter { it.second != 0 }
            .map { it.first.toDouble() / it.second }
            .ifEmpty { listOf(1.0) }
            .average()
    }

    private operator fun plus(result: JacocoCoverageResult) =
        JacocoCoverageResult(
            totalMethods + result.totalMethods,
            missedMethods + result.missedMethods,
            totalLines + result.totalLines,
            missedLines + result.missedLines,
            totalBranches + result.totalBranches,
            missedBranches + result.missedBranches
        )

    private fun visitedMethods() = totalMethods - missedMethods
    private fun visitedLines() = totalLines - missedLines
    private fun visitedBranches() = totalBranches - missedBranches

    private fun methodsPercent() = if (totalMethods == 0) MAX_PERCENT else MAX_PERCENT * visitedMethods() / totalMethods
    private fun linesPercent() = if (totalLines == 0) MAX_PERCENT else MAX_PERCENT * visitedLines() / totalLines
    private fun branchesPercent() =
        if (totalBranches == 0) MAX_PERCENT else MAX_PERCENT * visitedBranches() / totalBranches

    companion object {
        fun sum(results: Collection<JacocoCoverageResult>) = results
            .reduce { a, b -> a + b }
    }
}

private const val MAX_PERCENT = 100.0
