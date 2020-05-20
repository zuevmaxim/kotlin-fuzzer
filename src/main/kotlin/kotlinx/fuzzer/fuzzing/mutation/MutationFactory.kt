package kotlinx.fuzzer.fuzzing.mutation

import kotlinx.fuzzer.fuzzing.Logger
import kotlinx.fuzzer.fuzzing.storage.Storage

/**
 * All mutations container.
 * @see  <a href="https://github.com/dvyukov/go-fuzz">go-fuzz</a>
 */
class MutationFactory(storage: Storage) : Mutation {
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

    override fun mutate(bytes: ByteArray) = mutations.random().mutate(bytes)

    /** Returns mutated byte array or null if it is not enough memory. */
    private fun newMutation(bytes: ByteArray): ByteArray? {
        return try {
            var mutated: ByteArray
            do {
                mutated = mutate(bytes)
            } while (mutated === bytes)
            mutated
        } catch (e: OutOfMemoryError) {
            Logger.debug("OutOfMemoryError")
            null
        }
    }

    /** Mutate [bytes] [count] times. */
    fun mutate(bytes: ByteArray, count: Int) = generateSequence { newMutation(bytes) }.filterNotNull().take(count)

}
