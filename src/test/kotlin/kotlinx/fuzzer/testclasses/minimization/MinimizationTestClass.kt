package kotlinx.fuzzer.testclasses.minimization

class MinimizationTestClass {
    fun test(bytes: ByteArray): Int {
        if (bytes.size >= 2) {
            error("Big input")
        } else {
            error("Small input")
        }
    }
}
