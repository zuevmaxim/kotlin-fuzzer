package ru.example.kotlinfuzzer

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import kotlin.reflect.full.cast

internal class InstanceCreatorTest {

    private fun testClassCreation(clazz: Class<*>) {
        val x = InstanceCreator.create(clazz)
        assertNotNull(x)
        clazz.kotlin.cast(x) // throws if type is incorrect
    }

    private fun testClassesCreation(list: List<Class<*>>) =
        list.forEach { clazz -> testClassCreation(clazz) }

    @Test
    fun primitivesTest() = testClassesCreation(
        listOf(
            Int::class.java,
            Long::class.java,
            Double::class.java,
            Float::class.java,
            Char::class.java,
            String::class.java,
            Byte::class.java,
            ByteArray::class.java
        )
    )


    data class IntTestClass(val x: Int)
    data class LongDoubleTestClass(val x: Long, val y: Double)

    @Test
    fun severalArgumentsTest() = testClassesCreation(
        listOf(IntTestClass::class.java, LongDoubleTestClass::class.java)
    )

    class EmptyArgumentsTestClass

    @Test
    fun emptyArgumentsTest() = testClassCreation(EmptyArgumentsTestClass::class.java)
}
