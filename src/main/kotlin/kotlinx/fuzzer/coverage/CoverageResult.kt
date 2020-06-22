package kotlinx.fuzzer.coverage

data class CoverageResult(
    val totalMethods: Int, val missedMethods: Int,
    val totalLines: Int, val missedLines: Int,
    val totalBranches: Int, val missedBranches: Int
) {
    override fun toString(): String {
        fun missedLine(unit: String, total: Int, missed: Int) = "$missed of $total $unit missed"
        return """
            |${missedLine("methods", totalMethods, missedMethods)}
            |${missedLine("lines", totalLines, missedLines)}
            |${missedLine("branches", totalBranches, missedBranches)}
        """.trimMargin()
    }

    fun percent() = listOf(methodsPercent(), linesPercent(), branchesPercent()).average()

    operator fun compareTo(other: CoverageResult) = percent().compareTo(other.percent())

    fun otherCoverageRatio(other: CoverageResult) = listOf(
        visitedMethods() to other.visitedMethods(),
        visitedLines() to other.visitedLines(),
        visitedBranches() to other.visitedBranches()
    )
        .filter { it.second != 0 }
        .map { it.first.toDouble() / it.second }
        .ifEmpty { listOf(1.0) }
        .average()

    private operator fun plus(result: CoverageResult) = CoverageResult(
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
    private fun branchesPercent() = if (totalBranches == 0) MAX_PERCENT else MAX_PERCENT * visitedBranches() / totalBranches

    companion object {
        fun sum(results: Collection<CoverageResult>) = results
            .reduce { a, b -> a + b }
    }
}

private const val MAX_PERCENT = 100.0
