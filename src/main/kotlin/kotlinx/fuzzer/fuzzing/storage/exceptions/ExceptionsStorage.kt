package kotlinx.fuzzer.fuzzing.storage.exceptions

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/** Exceptions storage filters repeated stacktrace exception. */
class ExceptionsStorage {
    private val exceptions = Collections.newSetFromMap(ConcurrentHashMap<StacktraceEqualException, Boolean>())

    fun add(exception: Throwable): Boolean {
        val wrapper = StacktraceEqualException(exception)
        return exceptions.add(wrapper)
    }
}
