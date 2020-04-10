package ru.example.kotlinfuzzer.fuzzing.inputhandlers

abstract class InputHandler<T> {
    protected val next = hashMapOf<Class<*>, InputHandler<*>>()

    fun <U> nextHandler(clazz: Class<U>, handler: InputHandler<U>) {
        next[clazz] = handler
    }

    protected inline fun <reified U> onResult(result: U) {
        @Suppress("UNCHECKED_CAST") // nextHandler could be called only with equal type parameters
        val nextHandler = next[U::class.java] as InputHandler<U>?
        nextHandler?.run(result)
    }

    abstract fun run(input: T)
}
