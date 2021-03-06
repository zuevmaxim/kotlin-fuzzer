package ru.example.kotlinfuzzer

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
}
