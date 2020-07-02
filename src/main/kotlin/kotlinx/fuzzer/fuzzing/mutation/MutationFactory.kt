package kotlinx.fuzzer.fuzzing.mutation

import kotlinx.fuzzer.fuzzing.log.Logger
import kotlinx.fuzzer.fuzzing.storage.Storage

/**
 * All mutations container.
 * @see  <a href="https://github.com/dvyukov/go-fuzz">go-fuzz</a>
 */
class MutationFactory(storage: Storage) {
    private val mutations = listOf(
        InsertBytesMutation(),
        InsertCharsMutation(),
        RemoveBytesMutation(),
        ReplaceByteMutation(),
        ReplaceCharMutation(),
        DuplicateRangeMutation(),
        CopyRangeMutation(),
        BitFlipMutation(),
        SwapBytesMutation(),
        ReplaceDigitMutation(),
        AddSubtractByteMutation(),
        AddSubtractCharMutation(),
        AddSubtractIntMutation(),
        AddSubtractLongMutation(),
        ReplaceInterestingByteMutation(),
        ReplaceInterestingCharMutation(),
        ReplaceInterestingIntMutation(),
        ReplaceInterestingLongMutation(),
        InsertAnotherInputMutation(storage),
        SpliceAnotherInputMutation(storage),
        ReplaceTextNumberMutation()
    )

    /** Returns mutated byte array or null if it is not enough memory. */
    private fun newMutation(bytes: ByteArray) = try {
        mutations.random().mutate(bytes)
    } catch (e: OutOfMemoryError) {
        Logger.debug("OutOfMemoryError")
        null
    }

    /** Generate sequence of [count] mutations of [bytes]. */
    fun mutate(bytes: ByteArray, count: Int) = generateSequence { newMutation(bytes) }.filterNotNull().take(count)

}
