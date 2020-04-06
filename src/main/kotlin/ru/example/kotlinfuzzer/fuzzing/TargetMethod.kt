package ru.example.kotlinfuzzer.fuzzing

import ru.example.kotlinfuzzer.coverage.InstanceCreator
import ru.example.kotlinfuzzer.fuzzing.input.Input
import java.lang.reflect.Method
import kotlin.reflect.full.cast

class TargetMethod(private val targetClass: Class<*>, methodName: String) {
    private val method: Method

    init {
        val methods = targetClass.declaredMethods
            .filter { it.name == methodName }
            .filter { checkMethodSignature(it) }
        check(methods.isNotEmpty()) { "Method $methodName with correct signature not found!" }
        method = methods[0]
    }

    private fun checkMethodSignature(method: Method): Boolean {
        if (method.returnType != Int::class.java) return false
        if (method.parameterCount != 1) return false
        if (method.parameterTypes[0] != ByteArray::class.java) return false
        return true
    }

    fun execute(input: Input, callback: (Result<Int>) -> Unit) {
        val targetInstance = InstanceCreator.create(targetClass)
        val result: Result<Int> = try {
            val output = method.invoke(targetInstance, input.data)
            val intOutput = Int::class.cast(output)
            Result.success(intOutput)
        } catch (e: Throwable) {
            Result.failure(e)
        }
        callback(result)
    }

}
