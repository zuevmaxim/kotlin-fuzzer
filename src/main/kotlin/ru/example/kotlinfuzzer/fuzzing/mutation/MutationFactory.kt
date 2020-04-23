package ru.example.kotlinfuzzer.fuzzing.mutation

import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import kotlin.random.Random

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
    )

    private fun randomMutation(): Mutation {
        val index = Random.nextInt(mutations.size)
        return mutations[index]
    }

    override fun mutate(bytes: ByteArray): ByteArray {
        val mutation = randomMutation()
        return mutation.mutate(bytes)
    }

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
