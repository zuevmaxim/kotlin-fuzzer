package ru.example.kotlinfuzzer.fuzzing.mutation

import kotlin.random.Random

object MutationFactory : Mutation {
    private val mutations: List<Mutation> = listOf(
        AddCharMutation(),
        AddByteMutation(),
        RemoveBytesMutation(),
        ReplaceByteMutation(),
        ReplaceCharMutation()
    )

    private fun randomMutation(): Mutation {
        val index = Random.nextInt(mutations.size)
        return mutations[index]
    }

    override fun mutate(bytes: ByteArray): ByteArray {
        val mutation = randomMutation()
        return mutation.mutate(bytes)
    }

}
