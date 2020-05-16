package kotlinx.fuzzer.fuzzing.mutation

/** Input mutation. */
interface Mutation {
    /**
     * Mutate input.
     * @param bytes immutable input
     * @return new input bytes of the same array if no mutation is possible
     */
    fun mutate(bytes: ByteArray): ByteArray
}
