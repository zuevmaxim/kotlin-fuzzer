package kotlinx.fuzzer.tests.ktor

class Result<T>(code: () -> T) {
    var result: T? = null
        private set
    var exception: Throwable? = null
        private set

    init {
        try {
            result = code()
        } catch (t: Throwable) {
            exception = t
        }
    }

    fun fail() = exception != null || result == null
}
