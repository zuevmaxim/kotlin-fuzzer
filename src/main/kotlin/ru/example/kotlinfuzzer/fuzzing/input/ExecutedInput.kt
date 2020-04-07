package ru.example.kotlinfuzzer.fuzzing.input

import ru.example.kotlinfuzzer.coverage.CoverageResult

class ExecutedInput(
    val data: ByteArray,
    private val executionTimeMs: Long,
    private val coverageResult: CoverageResult,
    private val userPriority: Int
) : ByteArrayHash(data) {
    fun priority(): Int {
        // TODO use execution time, user priority, length?
        return coverageResult.percent().toInt()
    }
}
