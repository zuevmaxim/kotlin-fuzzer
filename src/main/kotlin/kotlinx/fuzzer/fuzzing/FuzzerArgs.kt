package kotlinx.fuzzer.fuzzing

data class FuzzerArgs(
    val className: String,
    val methodName: String,
    val workingDirectory: String,
    val classpath: List<String> = emptyList(),
    val packages: List<String>,
    val maxTaskQueueSize: Int = Fuzzer.MAX_TASK_QUEUE_SIZE,
    val threadsNumber: Int = Runtime.getRuntime().availableProcessors(),
    val compositeCoverageCount: Int = 1,
    val ignoreEqualStackTrace: Boolean = Fuzzer.DEFAULT_IGNORE_EQUAL_EXCEPTIONS
) {
    init {
        require(threadsNumber >= 1) { "Number of threads should be at least one." }
        require(maxTaskQueueSize >= 1) { "Size of queue should be at least one." }
    }
}
