package ru.example.kotlinfuzzer

@Suppress("unused", "UNUSED_PARAMETER", "NOTHING_TO_INLINE")
internal class TestClass {

    private inline fun currentFunctionName() = Thread.currentThread().stackTrace[1].methodName
    private inline fun register() = MethodRunnerTest.doneMethods.add(currentFunctionName())

    fun testNoArg() = register()
    fun testOneArg(bytes: ByteArray) = register()
    fun testTwoArgs(x: Int, y: Double) = register()
    fun testStringArgs(string: String) = register()
}
