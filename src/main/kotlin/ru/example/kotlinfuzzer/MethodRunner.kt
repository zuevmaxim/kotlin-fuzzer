package ru.example.kotlinfuzzer

import kotlin.reflect.full.valueParameters

class MethodRunner(
    path: String,
    className: String
) {
    private val loader = MyClassLoader(path, className)

    fun run(methodName: String) {
        val instance = loader.createInstance()
        val methods = loader.getMethods(methodName)
        if (methods == null || methods.isEmpty()) error("Method not found: $methodName")
        for (method in methods) {
            val arguments = InstanceCreator.createParameters(method.valueParameters)
            method.call(instance, *arguments)
        }
    }
}
