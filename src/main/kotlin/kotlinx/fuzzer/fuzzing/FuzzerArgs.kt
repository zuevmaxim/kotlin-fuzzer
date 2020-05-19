package kotlinx.fuzzer.fuzzing

data class FuzzerArgs(
    val className: String,
    val methodName: String,
    val workingDirectory: String,
    val classpath: List<String>,
    val packages: List<String>,
    val maxTaskQueueSize: Int,
    val threadsNumber: Int
) {
    init {
        require(threadsNumber >= 1) { "Number of threads should be at least one." }
    }
}
