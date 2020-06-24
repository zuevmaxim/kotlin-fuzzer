package kotlinx.fuzzer.fuzzing.storage.exceptions

internal data class MyStackTraceElement(
    val fileName: String?, val lineNumber: Int, val className: String, val methodName: String
) {
    constructor(element: StackTraceElement) : this(element.fileName, element.lineNumber, element.className, element.methodName)
}

/** Wrapper for comparing exceptions by stacktrace. */
class StacktraceEqualException(private val e: Throwable) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is StacktraceEqualException) return false
        var e1: Throwable? = e
        var e2: Throwable? = other.e
        do {
            if (e1!!.javaClass != e2!!.javaClass
                || !equalStackTrace(e1.stackTrace, e2.stackTrace)
            ) {
                return false
            }
            e1 = e1.cause
            e2 = e2.cause
        } while (e1 != null && e2 != null)
        return e1 == null && e2 == null
    }

    private fun equalStackTrace(trace1: Array<StackTraceElement?>?, trace2: Array<StackTraceElement?>?): Boolean {
        if (trace1 == null || trace2 == null) return false
        val filtered1 = trimReflection(trace1).map { MyStackTraceElement(it) }
        val filtered2 = trimReflection(trace2).map { MyStackTraceElement(it) }
        if (filtered1.size != filtered2.size) return false
        for (i in filtered1.indices) {
            if (filtered1[i] != filtered2[i]) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 42
        var exception: Throwable? = e
        do {
            val stackTrace = exception!!.stackTrace ?: return result
            val filtered = trimReflection(stackTrace).map { MyStackTraceElement(it) }
            for (i in filtered.indices) {
                result += i * filtered[i].hashCode()
            }
            exception = exception.cause
        } while (exception != null)
        return result
    }

    private companion object {
        /**
         * Cut stack trace up to reflection call.
         * Further trace may differ because of reflection classes or because of applying minimization.
         */
        // TODO: User may use reflection. Should trim only last usage.
        private fun trimReflection(trace: Array<StackTraceElement?>) = trace
            .filterNotNull()
            .takeWhile { element -> !element.className.contains("jdk.internal.reflect") }
    }
}
