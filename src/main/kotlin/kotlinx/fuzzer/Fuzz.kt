package kotlinx.fuzzer

/** Fuzz method annotation specifies method to execute while fuzzing. */
@Target(AnnotationTarget.FUNCTION)
annotation class Fuzz(
    val workingDirectory: String,
    val packages: Array<String> = [],
    val classpath: Array<String> = [],
    val maxTaskQueueSize: Int = Fuzzer.MAX_TASK_QUEUE_SIZE
)
