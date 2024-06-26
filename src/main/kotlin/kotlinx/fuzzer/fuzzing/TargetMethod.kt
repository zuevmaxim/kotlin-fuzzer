package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.coverage.InstanceCreator
import kotlinx.fuzzer.fuzzing.input.Input
import java.lang.reflect.Method
import kotlin.reflect.full.cast

/**
 * Method tested with fuzzer.
 * Should have signature: fun fuzz(bytes: ByteArray): Int.
 */
class TargetMethod(private val targetClass: Class<*>, methodName: String) {
    private val method = targetClass.declaredMethods
        .filter { it.name == methodName }
        .singleOrNull { isApplicableMethodSignature(it) }
        ?: throw IllegalArgumentException("Single method $methodName with correct signature not found!")

    fun execute(input: Input): Result<Int> {
        val targetInstance = InstanceCreator.constructDefault(targetClass)
        return runCatching {
            val output = method.invoke(targetInstance, input.data)
            Int::class.cast(output)
        }
    }

    private fun isApplicableMethodSignature(method: Method): Boolean {
        if (method.returnType != Int::class.java) return false
        if (method.parameterCount != 1) return false
        if (method.parameterTypes[0] != ByteArray::class.java) return false
        return true
    }
}
