package ru.example.kotlinfuzzer.coverage

import org.jacoco.core.analysis.IClassCoverage

data class CoverageResult(
    val totalMethods: Int, val missedMethods: Int,
    val totalLines: Int, val missedLines: Int,
    val totalBranches: Int, val missedBranches: Int
) {
    constructor(result: IClassCoverage) : this(
        result.methodCounter.totalCount, result.methodCounter.missedCount,
        result.lineCounter.totalCount, result.lineCounter.missedCount,
        result.branchCounter.totalCount, result.branchCounter.missedCount
    )

    private operator fun plus(result: CoverageResult) = CoverageResult(
        totalMethods + result.totalMethods,
        missedMethods + result.missedMethods,
        totalLines + result.totalLines,
        missedLines + result.missedLines,
        totalBranches + result.totalBranches,
        missedBranches + result.missedBranches
    )

    companion object {
        fun sum(results: Collection<IClassCoverage>) = results
            .map { CoverageResult(it) }
            .reduce { a, b -> a + b }
    }

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
    fun ratio(other: CoverageResult) = listOf(
        visitedMethods() to other.visitedMethods(),
        visitedLines() to other.visitedLines(),
        visitedBranches() to other.visitedBranches()
    )
        .filter { it.second != 0 }
        .map { it.first.toDouble() / it.second }
        .ifEmpty { listOf(1.0) }
        .average()

    private fun visitedMethods() = totalMethods - missedMethods
    private fun visitedLines() = totalLines - missedLines
    private fun visitedBranches() = totalBranches - missedBranches

    private fun methodsPercent() = if (totalMethods == 0) MAX_PERCENT else MAX_PERCENT * visitedMethods() / totalMethods
    private fun linesPercent() = if (totalLines == 0) MAX_PERCENT else MAX_PERCENT * visitedLines() / totalLines
    private fun branchesPercent() = if (totalBranches == 0) MAX_PERCENT else MAX_PERCENT * visitedBranches() / totalBranches
}

private const val MAX_PERCENT = 100.0