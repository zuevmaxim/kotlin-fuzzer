package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.FuzzCrash
import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.coverage.InstanceCreator
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/** Defines the way of logging corpus inputs and crashes. */
interface StorageStrategy {
    fun save(input: ExecutedInput): Boolean
    fun save(input: FailInput): Boolean
}

/**
 * Create strategy from users callback or [FilesStorageStrategy] if [saveCrash] is true or throw on crash.
 * User's strategy uses callback to inform about a crash.
 */
internal fun createStorageStrategy(
    clazz: Class<*>,
    workingDirectory: String,
    saveCrash: Boolean
) = when {
    classContainsCallback(clazz) -> CallbackStorageStrategy(clazz)
    saveCrash -> FilesStorageStrategy(File(workingDirectory), Fuzzer.DEFAULT_SAVE_CORPUS)
    else -> ThrowStorageStrategy()
}

/** Strategy that uses a callback for informing about a crash. Uses [FuzzCrash] annotation. */
private class CallbackStorageStrategy(callbackClass: Class<*>) : StorageStrategy {
    private val instance = InstanceCreator.constructDefault(callbackClass)
    private val callback: Method

    init {
        val callbacks = callbackClass.declaredMethods
            .filter { it.getAnnotation(FuzzCrash::class.java) != null }
        require(callbacks.isNotEmpty()) { "No FuzzCrash callbacks found." }
        callback = callbacks.singleOrNull()
            ?: throw IllegalArgumentException("One method with FuzzCrash annotation expected.")
        require(callback.returnType.kotlin == Void::class) { "Unit return type in FuzzCrash callback expected, but ${callback.returnType.kotlin} found." }
        require(callback.parameterCount == 2) { "Two arguments FuzzCrash callback expected, but ${callback.parameterCount} found." }
        require(callback.parameters[0].type == Throwable::class.java) { "Throwable argument expected in FuzzCrash callback, but ${callback.parameters[0].type.name} found." }
        require(callback.parameters[1].type == ByteArray::class.java) { "ByteArray argument expected in FuzzCrash callback, but ${callback.parameters[1].type.name} found." }
    }

    override fun save(input: ExecutedInput) = true

    override fun save(input: FailInput): Boolean {
        try {
            callback.invoke(instance, input.e, input.data)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
        return true
    }
}

private fun classContainsCallback(clazz: Class<*>) = clazz.declaredMethods
    .any { it.getAnnotation(FuzzCrash::class.java) != null }

/** This strategy throws an exception which was found by fuzzer. */
private class ThrowStorageStrategy : StorageStrategy {
    override fun save(input: ExecutedInput) = true
    override fun save(input: FailInput) = throw input.e
}
