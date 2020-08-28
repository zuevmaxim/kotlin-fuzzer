package kotlinx.fuzzer.fuzzing.inputhandlers

/**
 * Tries to drop a range of bytes. Uses greedy algorithm.
 * Produces O((size of [array])^2) [isValidArray] executions.
 * Assumes that [isValidArray] returns true on [array].
 * @param timeoutMs timeout for minimization execution(as minimization could run too long time and slow down fuzzer)
 */
inline fun minimizeArray(array: ByteArray, isValidArray: (ByteArray) -> Boolean, timeoutMs: Long): ByteArray {
    val start = System.currentTimeMillis()
    assert(isValidArray(array))
    var currentArray = array
    for (dropLength in array.size downTo 1) {
        if (currentArray.size < dropLength) continue
        var candidate = ByteArray(currentArray.size - dropLength)
        var dropIndex = 0
        while (dropIndex <= currentArray.size - dropLength) {
            if (System.currentTimeMillis() - start > timeoutMs) {
                return currentArray
            }
            if (candidate.size != currentArray.size - dropLength) {
                candidate = ByteArray(currentArray.size - dropLength)
            }
            currentArray.copyInto(candidate, endIndex = dropIndex)
            if (dropIndex != currentArray.size - dropLength) {
                currentArray.copyInto(candidate, destinationOffset = dropIndex, startIndex = dropIndex + dropLength)
            }
            if (isValidArray(candidate)) {
                currentArray = candidate
            } else {
                dropIndex++
            }
        }
    }
    return currentArray
}

inline fun minimizeArray(array: ByteArray, isValidArray: (ByteArray) -> Boolean) =
    minimizeArray(array, isValidArray, DEFAULT_TIMEOUT_MS)

/** Default minimization timeout is 1 second. */
const val DEFAULT_TIMEOUT_MS = 1000L
