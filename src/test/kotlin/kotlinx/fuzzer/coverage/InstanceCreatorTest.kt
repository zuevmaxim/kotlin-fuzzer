package kotlinx.fuzzer.coverage

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.reflect.full.cast

internal class InstanceCreatorTest {

    private fun testClassCreation(clazz: Class<*>) {
        val x = InstanceCreator.create(clazz)
        assertNotNull(x)
        clazz.kotlin.cast(x) // throws if type is incorrect
    }

    @ParameterizedTest
    @ValueSource(
        classes = [Int::class, Long::class, Double::class, Float::class, Char::class,
            String::class, Byte::class, ByteArray::class]
    )
    fun primitivesTest(clazz: Class<*>) = testClassCreation(clazz)

    data class IntTestClass(val x: Int)
    data class LongDoubleTestClass(val x: Long, val y: Double)

    @ParameterizedTest
    @ValueSource(classes = [IntTestClass::class, LongDoubleTestClass::class])
    fun severalArgumentsTest(clazz: Class<*>) = testClassCreation(clazz)

    class EmptyArgumentsTestClass

    @Test
    fun emptyArgumentsTest() = testClassCreation(EmptyArgumentsTestClass::class.java)

    @Suppress("UNUSED_PARAMETER")
    class IllegalClass(unused: IntTestClass)

    @Test
    fun illegalClassCreationFails() {
        assertThrows(IllegalStateException::class.java) { testClassCreation(IllegalClass::class.java) }
    }

}
