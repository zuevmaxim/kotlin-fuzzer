package kotlinx.fuzzer.coverage

import kotlinx.fuzzer.testclasses.coverage.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class CoverageTest {
    companion object {
        private val coverageRunner = createCoverageRunner(emptyList(), listOf("kotlinx.fuzzer.testclasses.coverage"))
    }

    @Test
    fun singleCodeTest() {
        assertEquals(0.0, coverageRunner.runWithCoverage { singleCode() }.score())
    }

    @Test
    fun ifCodeTest() {
        assertEquals(1.0, coverageRunner.runWithCoverage { ifCode(-1) }.score())
        assertEquals(1.0, coverageRunner.runWithCoverage { ifCode(1) }.score())
    }

    @Test
    fun forCodeTest() {
        assertEquals(1.0, coverageRunner.runWithCoverage { forCode(0) }.score())
        assertEquals(2.0, coverageRunner.runWithCoverage { forCode(1) }.score())
        assertEquals(3.0, coverageRunner.runWithCoverage { forCode(2) }.score())
        assertEquals(3.0, coverageRunner.runWithCoverage { forCode(3) }.score())
        assertEquals(coverageRunner.runWithCoverage { forCode(2) }, coverageRunner.runWithCoverage { forCode(3) })
    }

    @Test
    fun tryCatchTest() {
        val count = listOf(0, 1, 2)
            .map { coverageRunner.runWithCoverage { switchCode(it) } }
            .onEach { assertEquals(1.0, it.score()) }
            .toSet()
            .size
        assertEquals(3, count)
        assertEquals(2.0, coverageRunner.runWithCoverage { tryCatchCode(0) }.score())
        assertEquals(2.0, coverageRunner.runWithCoverage { tryCatchCode(1) }.score())
        assertEquals(3.0, coverageRunner.runWithCoverage { tryCatchCode(2) }.score())
    }

    @Test
    fun abcdTestParallel() {
        val size = 1000
        val threadPool = Executors.newFixedThreadPool(8)
        listOf("a", "b", "ab", "abc", "", "c", "x", "mcg")
            .map { s -> { coverageRunner.runWithCoverage { abcd(s.toByteArray()) } } }
            .map { f -> f to f() }
            .map { (f, result) ->
                {
                    List(size) { f }
                        .parallelStream()
                        .forEach {
                            assertEquals(result, it())
                        }
                }
            }
            .map { threadPool.submit(it) }
            .forEach { it.get() }
    }

    @Test
    fun switchTest() {
        val count = listOf(0, 1, 2, 3, 4, 5)
            .map { coverageRunner.runWithCoverage { switchCode(it) } }
            .onEach { assertEquals(1.0, it.score()) }
            .toSet()
            .size
        assertEquals(5, count)
    }

    @Test
    fun lookupSwitchTest() {
        val count = listOf(0, 1, 2, 10, 100, 1000)
            .map { coverageRunner.runWithCoverage { lookupSwitchCode(it) } }
            .onEach { assertEquals(1.0, it.score()) }
            .toSet()
            .size
        assertEquals(5, count)
    }

    @Test
    fun abcdTest() {
        assertEquals(2.0, coverageRunner.runWithCoverage { abcd("".toByteArray()) }.score())
        assertEquals(3.0, coverageRunner.runWithCoverage { abcd("x".toByteArray()) }.score())
        assertEquals(3.0, coverageRunner.runWithCoverage { abcd("xx".toByteArray()) }.score())
        assertEquals(4.0, coverageRunner.runWithCoverage { abcd("a".toByteArray()) }.score())
        assertEquals(5.0, coverageRunner.runWithCoverage { abcd("ax".toByteArray()) }.score())
        assertEquals(5.0, coverageRunner.runWithCoverage { abcd("axb".toByteArray()) }.score())
        assertEquals(5.0, coverageRunner.runWithCoverage { abcd("axbc".toByteArray()) }.score())
        assertEquals(6.0, coverageRunner.runWithCoverage { abcd("ab".toByteArray()) }.score())
        assertEquals(7.0, coverageRunner.runWithCoverage { abcd("abx".toByteArray()) }.score())
        assertEquals(8.0, coverageRunner.runWithCoverage { abcd("abc".toByteArray()) }.score())
        assertEquals(9.0, coverageRunner.runWithCoverage { abcd("abcx".toByteArray()) }.score())
        assertEquals(9.0, coverageRunner.runWithCoverage { abcd("abcz".toByteArray()) }.score())
    }

    @Test
    fun recursionTest() {
        assertEquals(1.0, coverageRunner.runWithCoverage { recursion(0) }.score())
        assertEquals(2.0, coverageRunner.runWithCoverage { recursion(1) }.score())
        assertEquals(2.0, coverageRunner.runWithCoverage { recursion(2) }.score())
        assertEquals(2.0, coverageRunner.runWithCoverage { recursion(3) }.score())
    }

    @Test
    fun multiThreadTest() {
        assertEquals(0.0, coverageRunner.runWithCoverage { assertEquals(1, multiThread(0, 1)) }.score())
        assertEquals(0.0, coverageRunner.runWithCoverage { assertEquals(2, multiThread(0, 0)) }.score())
        assertEquals(0.0, coverageRunner.runWithCoverage { assertEquals(3, multiThread(1, 1)) }.score())
        assertEquals(0.0, coverageRunner.runWithCoverage { assertEquals(4, multiThread(1, -1)) }.score())
    }
}
