package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.FuzzCrash
import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.coverage.InstanceCreator
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Hash
import java.io.File
import java.lang.reflect.InvocationTargetException

/** Defines the way of logging corpus inputs and crashes. */
interface StorageStrategy {
    fun save(input: ExecutedInput): Boolean
    fun save(input: FailInput, hash: Hash): Boolean
}

/**
 * Create strategy from users callback or [FilesStorageStrategy].
 * User's strategy uses callback to inform about a crash.
 */
internal fun createStorageStrategy(clazz: Class<*>, workingDirectory: String): StorageStrategy {
    val callbacks = clazz.declaredMethods
        .filter { it.getAnnotation(FuzzCrash::class.java) != null }
    if (callbacks.isEmpty()) {
        return FilesStorageStrategy(File(workingDirectory), Fuzzer.DEFAULT_SAVE_CORPUS)
    }
    val callback = callbacks.singleOrNull()
        ?: throw IllegalArgumentException("One method with FuzzCrash annotation expected.")
    require(callback.returnType.kotlin == Void::class) { "Unit return type in FuzzCrash callback expected, but ${callback.returnType.kotlin} found." }
    require(callback.parameterCount == 2) { "Two arguments FuzzCrash callback expected." }
    require(callback.parameters[0].type == Throwable::class.java) { "Throwable argument expected in FuzzCrash callback." }
    require(callback.parameters[1].type == ByteArray::class.java) { "ByteArray argument expected in FuzzCrash callback." }
    return object : StorageStrategy {
        private val instance = InstanceCreator.constructDefault(clazz)
        override fun save(input: ExecutedInput) = true

        override fun save(input: FailInput, hash: Hash): Boolean {
            try {
                callback.invoke(instance, input.e, input.data)
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
            return true
        }
    }
}
