package kotlinx.fuzzer.fuzzing.mutation

/** Input mutation. */
interface Mutation {
    /**
     * Mutate input.
     * @param bytes immutable input
     * @return new input bytes or null if no mutation is possible
     */
    fun mutate(bytes: ByteArray): ByteArray?
}
