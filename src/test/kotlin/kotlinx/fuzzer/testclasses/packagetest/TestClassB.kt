package kotlinx.fuzzer.testclasses.packagetest


@Suppress("unused", "UNUSED_PARAMETER")
internal class TestClassB {
    private val a = TestClassA()

    fun testRunning(bytes: ByteArray) = a.testRunning(bytes)

    fun coverageTest(bytes: ByteArray) = a.coverageTest(bytes)
}
