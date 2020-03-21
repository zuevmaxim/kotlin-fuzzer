package ru.example.kotlinfuzzer.testclasses.packagetest

import ru.example.kotlinfuzzer.PackageMethodRunnerTest


@Suppress("NOTHING_TO_INLINE", "unused", "UNUSED_PARAMETER")
internal class TestClassA {
    private inline fun currentFunctionName() = Thread.currentThread().stackTrace[1].methodName
    private inline fun register() = PackageMethodRunnerTest.doneMethods.add(currentFunctionName())

    fun testNoArg() = register()
    fun testOneArg(bytes: ByteArray) = register()
    fun testTwoArgs(x: Int, y: Double) = register()
    fun testStringArgs(string: String) = register()

    fun coverageTest(x: Int, y: Int) = when {
        x == y -> "$x equals $y"
        x < y -> "$x less than $y"
        x > y -> "$x grater than $y"
        else -> "Something is wrong"
    }
}
