package kotlinx.fuzzer.testclasses.packagetest

import kotlinx.fuzzer.coverage.PackageMethodRunnerTest
import java.nio.ByteBuffer

@Suppress("NOTHING_TO_INLINE", "unused", "UNUSED_PARAMETER")
internal class TestClassA {
    private inline fun currentFunctionName() = Thread.currentThread().stackTrace[1].methodName
    private inline fun register() = PackageMethodRunnerTest.doneMethods.add(currentFunctionName())

    fun testRunning(bytes: ByteArray) = register().let { 1 }

    fun coverageTest(bytes: ByteArray): Int {
        val buffer = ByteBuffer.wrap(bytes)
        val x = buffer.int
        val y = buffer.int
        return when {
            x == y -> 0
            x < y -> -1
            x > y -> 1
            else -> error("Something is wrong")
        }
    }
}
