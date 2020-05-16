package kotlinx.fuzzer.coverage

import java.lang.reflect.Parameter
import kotlin.random.Random

private const val MAX_ARRAY_SIZE = 1000

object InstanceCreator {
    /** Create instance of class. Should have public constructor with primitive arguments. */
    fun create(clazz: Class<*>?): Any? {
        if (clazz == null) return null
        createPrimitive(clazz)?.let { return@create it }
        for (constructor in clazz.constructors) {
            try {
                val arguments = createParameters(constructor.parameters)
                return constructor.newInstance(*arguments)
            } catch (e: Throwable) {
                // try other constructor
            }
        }
        error("Cannot create instance of type ${clazz.simpleName}")
    }

    private fun randomSize() = Random.nextInt(MAX_ARRAY_SIZE)

    private fun createParameters(parameters: Array<Parameter>) = parameters
        .map { createPrimitive(it.type) }
        .toTypedArray()

    private fun createPrimitive(clazz: Class<*>) = when (clazz) {
        ByteArray::class.java -> Random.nextBytes(randomSize())
        Byte::class.java -> Random.nextBytes(1)[0]
        Int::class.java -> Random.nextInt()
        Double::class.java -> Random.nextDouble()
        Float::class.java -> Random.nextFloat()
        Long::class.java -> Random.nextLong()
        Char::class.java -> Random.nextInt().toChar()
        String::class.java -> List(randomSize()) { Random.nextInt().toChar() }.joinToString("")
        else -> null
    }
}
