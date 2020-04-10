package ru.example.kotlinfuzzer.fuzzing.input

import ru.example.kotlinfuzzer.coverage.CoverageResult

class ExecutedInput(
    data: ByteArray,
    private val executionTimeMs: Long,
    val coverageResult: CoverageResult,
    val userPriority: Int
) : Input(data) {
    override fun priority(): Int {
        // TODO use execution time, user priority, length?
        return coverageResult.percent().toInt()
    }
}
