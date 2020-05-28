package kotlinx.fuzzer.tests.simple.utils

class StringTest {

    fun bigStepTest(bytes: ByteArray): Int {
        check(bytes.size < 20)
        return 1
    }

    fun localFunctionTest(bytes: ByteArray): Int {
        if (bytes.isEmpty()) {
            return -1
        }
        fun localFunction(index: Int) {
            if (0 <= index && index < bytes.size) {
                if (bytes[index] == 'x'.toByte()) {
                    error("Fail")
                }
            }
        }

        localFunction(bytes[0].toInt())
        return 1
    }
}
