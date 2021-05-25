package kotlinx.fuzzer

/** Fuzz method annotation specifies method to execute while fuzzing. */
@Target(AnnotationTarget.FUNCTION)
annotation class Fuzz(
    val workingDirectory: String,
    val packages: Array<String> = [],
    val classpath: Array<String> = []
)

/** FuzzCrash method annotation specifies method to handle crash while fuzz unit testing. */
@Target(AnnotationTarget.FUNCTION)
annotation class FuzzCrash
