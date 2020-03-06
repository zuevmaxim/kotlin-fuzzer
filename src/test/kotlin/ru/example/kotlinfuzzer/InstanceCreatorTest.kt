package ru.example.kotlinfuzzer

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

internal class InstanceCreatorTest {

    private fun testClassCreation(clazz: KClass<*>) {
        val x = InstanceCreator.create(clazz)
        assertNotNull(x)
        clazz.cast(x) // throws if type is incorrect
    }

    private fun testClassesCreation(list: List<KClass<*>>) =
        list.forEach { clazz -> testClassCreation(clazz) }

    @Test
    fun primitivesTest() = testClassesCreation(
        listOf(
            Int::class,
            Long::class,
            Double::class,
            Float::class,
            Char::class,
            String::class,
            Byte::class,
            ByteArray::class
        )
    )


    data class IntTestClass(val x: Int)
    data class LongDoubleTestClass(val x: Long, val y: Double)

    @Test
    fun severalArgumentsTest() = testClassesCreation(
        listOf(IntTestClass::class, LongDoubleTestClass::class)
    )

    private class PrivateTestClass

    @Test
    fun assertPrivateClassCreationFails() {
        assertThrows<IllegalStateException> { testClassCreation(PrivateTestClass::class) }
    }

    class EmptyArgumentsTestClass

    @Test
    fun emptyArgumentsTest() = testClassCreation(EmptyArgumentsTestClass::class)
}
