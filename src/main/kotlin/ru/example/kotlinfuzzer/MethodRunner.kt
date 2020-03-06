package ru.example.kotlinfuzzer

class MethodRunner(
    path: String,
    className: String,
    private val methodName: String
) {
    private val loader = MyClassLoader(path, className)

    fun run() {
        val instance = loader.createInstance()
        val method = loader.getMethod(methodName)
        method?.invoke(instance)
    }
}
