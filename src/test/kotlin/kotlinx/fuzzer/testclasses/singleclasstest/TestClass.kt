package kotlinx.fuzzer.testclasses.singleclasstest

import kotlinx.fuzzer.coverage.SingleClassCoverageRunnerTest
import java.nio.ByteBuffer
import kotlin.random.Random

@Suppress("unused", "UNUSED_PARAMETER", "NOTHING_TO_INLINE", "LiftReturnOrAssignment", "LiftReturnOrAssignment")
internal class TestClass {

    private inline fun currentFunctionName() = Thread.currentThread().stackTrace[1].methodName
    private inline fun register() = SingleClassCoverageRunnerTest.doneMethods.add(currentFunctionName())

    fun testRunning(bytes: ByteArray) = register().let { 1 }

    fun simpleCoverageTest(bytes: ByteArray): Int {
        val x = Random.nextInt()
        val y = Random.nextInt()
        return x + y
    }

    fun simpleTest(bytes: ByteArray) = 1

    fun coverageTest(bytes: ByteArray): Int {
        val buffer = ByteBuffer.wrap(bytes)
        val x = buffer.int
        val y = buffer.int
        return if (x % 2 == 0 && y % 2 == 0) {
            1
        } else if (x % 2 == 0 && y % 2 == 1) {
            2
        } else if (x % 2 == 1 && y % 2 == 0) {
            3
        } else {
            4
        }
    }

    fun conditionTest(bytes: ByteArray): Int {
        check(bytes.size != 2)
        require(bytes.size != 1)
        if (bytes.size == 4) return 1
        return 0
    }

    fun failTest(bytes: ByteArray): Int = error("Crash")
}
