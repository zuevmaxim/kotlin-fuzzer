package kotlinx.fuzzer.fuzzing.mutation

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

    /** Mutate [bytes] [count] times. */
    fun mutate(bytes: ByteArray, count: Int): Collection<ByteArray> {
        val result = hashSetOf<ByteArray>()
        while (result.size < count) {
            val mutated = mutate(bytes)
            if (mutated !== bytes) {
                result.add(mutated)
            }
        }
        return result
    }

}
