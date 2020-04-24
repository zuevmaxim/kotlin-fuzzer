package ru.example.kotlinfuzzer.fuzzing.mutation

import ru.example.kotlinfuzzer.fuzzing.storage.Storage

class MutationFactory(storage: Storage) : Mutation {
    private val mutations = listOf(
        InsertBytesMutation()
        , InsertCharsMutation()
        , RemoveBytesMutation()
        , ReplaceByteMutation()
        , ReplaceCharMutation()
        , DuplicateRangeMutation()
        , CopyRangeMutation()
        , BitFlipMutation()
        , SwapBytesMutation()
        , ReplaceDigitMutation()
        , AddSubtractByteMutation()
        , AddSubtractCharMutation()
        , AddSubtractIntMutation()
        , AddSubtractLongMutation()
        , ReplaceInterestingByteMutation()
        , ReplaceInterestingCharMutation()
        , ReplaceInterestingIntMutation()
        , ReplaceInterestingLongMutation()
        , InsertAnotherInputMutation(storage)
        , SpliceAnotherInputMutation(storage)
        , ReplaceTextNumberMutation()
    )

    override fun mutate(bytes: ByteArray) = mutations.random().mutate(bytes)

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
