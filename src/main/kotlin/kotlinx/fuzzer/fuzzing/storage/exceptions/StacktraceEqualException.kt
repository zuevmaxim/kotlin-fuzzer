package kotlinx.fuzzer.fuzzing.storage.exceptions

/**
 * Wrapper class to compare exceptions by their stacktraces.
 * Stacktraces are compared from the top to the reflective entry-point of the Fuzzer.
 */
internal class StacktraceEqualException(private val e: Throwable) {
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
        val filtered1 = trimReflection(trace1)
        val filtered2 = trimReflection(trace2)
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
            val filtered = trimReflection(stackTrace)
            for (i in filtered.indices) {
                result += i * filtered[i].hashCode()
            }
            exception = exception.cause
        } while (exception != null)
        return result
    }
}

/**
 * Trim stack trace up to fuzzer reflection call.
 * Further trace may differ because of reflection classes or because of applying minimization.
 */
private fun trimReflection(trace: Array<StackTraceElement?>): List<StackTraceElement> {
    var fuzzerReflectionStarted = false
    var fuzzerReflectionFinished = false
    var fuzzerTraceStarted = false
    return trace
            .filterNotNull()
            .reversed()
            .filter { element ->
                fuzzerTraceStarted = fuzzerTraceStarted || isFuzzerTrace(element)
                val reflectionTraceElement = isReflectionTrace(element)
                if (!fuzzerReflectionStarted && reflectionTraceElement && fuzzerTraceStarted) {
                    fuzzerReflectionStarted = true
                } else if (!fuzzerReflectionFinished && fuzzerReflectionStarted && !reflectionTraceElement) {
                    fuzzerReflectionFinished = true
                }
                fuzzerReflectionStarted && fuzzerReflectionFinished
            }
            .reversed()
}

private fun isReflectionTrace(element: StackTraceElement) = element.className.contains("jdk.internal.reflect")

private fun isFuzzerTrace(element: StackTraceElement) = element.className.contains("kotlinx.fuzzer")

fun Throwable.stackTraceEqualTo(other: Throwable) = StacktraceEqualException(this) == StacktraceEqualException(other)