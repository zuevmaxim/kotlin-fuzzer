package ru.example.kotlinfuzzer

import kotlin.random.Random

@Suppress("unused", "UNUSED_PARAMETER", "NOTHING_TO_INLINE", "LiftReturnOrAssignment", "LiftReturnOrAssignment")
internal class TestClass {

    private inline fun currentFunctionName() = Thread.currentThread().stackTrace[1].methodName
    private inline fun register() = MethodRunnerTest.doneMethods.add(currentFunctionName())

    fun testNoArg() = register()
    fun testOneArg(bytes: ByteArray) = register()
    fun testTwoArgs(x: Int, y: Double) = register()
    fun testStringArgs(string: String) = register()

    fun simpleCoverageTest(): Int {
        val x = Random.nextInt()
        val y = Random.nextInt()
        return x + y
    }

    fun coverageTest(x: Int, y: Int): String {
        if (x % 2 == 0 && y % 2 == 0) {
            return "$x and $y are even"
        } else if (x % 2 == 0 && y % 2 == 1) {
            return "$x is even but $y is odd"
        } else if (x % 2 == 1 && y % 2 == 0) {
            return "$x is odd but $y is even"
        } else {
            return "$x and $y are odd"
        }
    }
}
