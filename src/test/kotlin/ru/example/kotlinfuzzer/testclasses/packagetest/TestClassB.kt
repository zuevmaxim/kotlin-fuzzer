package ru.example.kotlinfuzzer.testclasses.packagetest


@Suppress("unused", "UNUSED_PARAMETER")
internal class TestClassB {
    private val a = TestClassA()

    fun testNoArg() = a.testNoArg()
    fun testOneArg(bytes: ByteArray) = a.testOneArg(bytes)
    fun testTwoArgs(x: Int, y: Double) = a.testTwoArgs(x, y)
    fun testStringArgs(string: String) = a.testStringArgs(string)

    fun coverageTest(x: Int, y: Int) = a.coverageTest(x, y)
}
