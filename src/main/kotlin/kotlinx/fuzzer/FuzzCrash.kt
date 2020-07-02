package kotlinx.fuzzer

/** FuzzCrash method annotation specifies method to handle crash while fuzz unit testing. */
@Target(AnnotationTarget.FUNCTION)
annotation class FuzzCrash
