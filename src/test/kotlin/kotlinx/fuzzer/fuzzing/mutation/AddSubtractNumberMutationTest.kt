package kotlinx.fuzzer.fuzzing.mutation

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class AddSubtractNumberMutationTest {

    @Test
    fun mutateByte() {
        val mutation = AddSubtractByteMutation()
        for (x in -5..5) {
            val data = byteArrayOf(x.toByte())
            val mutatedByte = mutation.mutate(data)!![0]
            assertTrue(x - MAX_ADD_SUBTRACT_MUTATION_VALUE <= mutatedByte && mutatedByte <= x + MAX_ADD_SUBTRACT_MUTATION_VALUE)
        }
    }
}
