package kotlinx.fuzzer.fuzzing.storage.exceptions

import java.util.*
import java.util.concurrent.*

/**
 * Storage that keeps track of all observed unique exceptions.
 * Exception uniqueness is determined by its stracktrace and the cause, the rest of the data is omitted
 * for pragmatic reasons: it's likely that exceptions with the same stacktraces but different messages just contain
 * diagnostic information in the message rather than they represent **different** failure modes.
 */
internal class ExceptionsStorage {
    private val exceptions = Collections.newSetFromMap(ConcurrentHashMap<StacktraceEqualException, Boolean>())

    /**
     * Tries to add exception to the storage, returns `false` if the same
     * exception was already recorded, `true` otherwise.
     */
    fun tryAdd(exception: Throwable): Boolean {
        val wrapper = StacktraceEqualException(exception)
        return exceptions.add(wrapper)
    }
}
