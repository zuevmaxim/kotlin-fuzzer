package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.fuzzing.Logger
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import org.apache.commons.lang3.ObjectUtils.max

class LocalStorage(private val storage: Storage, private val logger: Logger) {
    private var bestCoverage = storage.bestCoverage
    private val localCorpusInputs = mutableListOf<ExecutedInput>()
    private var crashesCount: Int = 0

    /** Save input to corpus if it's score is higher then current maximum. */
    fun save(input: ExecutedInput, force: Boolean = false) {
        val scoreUpdate = isBestInput(input)
        if (force || scoreUpdate) {
            bestCoverage = max(bestCoverage, input.coverageResult)
            if (storage.save(input) || force) {
                if (scoreUpdate) {
                    logger.log("Score update ${input.hash} coverage = ${bestCoverage.percent()}")
                }
                localCorpusInputs.add(input)
            }
        }
    }

    fun save(input: FailInput) {
        if (storage.save(input)) {
            crashesCount++
        }
    }

    fun synchronizeWithGlobalStorage() {
        storage.addCorpusInputs(localCorpusInputs, bestCoverage, crashesCount)
        localCorpusInputs.clear()
        crashesCount = 0
        bestCoverage = storage.bestCoverage
    }

    fun isBestInput(input: ExecutedInput): Boolean = input.coverageResult > bestCoverage
}
