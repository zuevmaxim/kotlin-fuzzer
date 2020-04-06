package ru.example.kotlinfuzzer.fuzzing.input

import ru.example.kotlinfuzzer.coverage.CoverageResult

class ExecutedInput(
    val data: ByteArray,
    private val executionTimeMs: Long,
    private val coverageResult: CoverageResult,
    private val userPriority: Int
) : ByteArrayHash(data) {
    fun priority(): Int {
        var priority = coverageResult.percent()
        if (executionTimeMs < 100) {
            priority *= 2
        }
        if (userPriority > 1) {
            priority *= 2
        }
        return priority.toInt()
    }
}
