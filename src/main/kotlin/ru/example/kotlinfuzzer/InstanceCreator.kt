package ru.example.kotlinfuzzer

import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

object InstanceCreator {

    private const val MAX_SIZE = 1000

    fun create(clazz: KClass<*>?): Any? {
        if (clazz == null) return null
        for (constructor in clazz.constructors) {
            try {
                val arguments = createParameters(constructor.parameters)
                return constructor.call(*arguments)
            } catch (e: Throwable) {
                // try other constructor
            }
        }
        error("Cannot create instance of type ${clazz.simpleName}")
    }

    private fun randomSize() = Random.nextInt(MAX_SIZE)

    fun createParameters(parameters: List<KParameter>) = parameters
        .map { createPrimitive(it.type.classifier as KClass<*>) }
        .toTypedArray()

    private fun createPrimitive(clazz: KClass<*>) = when (clazz) {
        ByteArray::class -> Random.nextBytes(randomSize())
        Int::class -> Random.nextInt()
        Double::class -> Random.nextDouble()
        Float::class -> Random.nextFloat()
        Long::class -> Random.nextLong()
        Char::class -> Random.nextInt().toChar()
        String::class -> List(randomSize()) { Random.nextInt().toChar() }.joinToString("")
        else -> error("Cannot create instance of type ${clazz.simpleName}")
    }
}
