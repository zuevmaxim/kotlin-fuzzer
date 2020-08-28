package kotlinx.fuzzer.testclasses.reflection

class LibraryClass {
    fun correctMethod(x: Int): Unit = error("Assert $x")
}

fun invoke(x: Int): Any = LibraryClass::class.java
        .getDeclaredMethod("correctMethod", Int::class.java)
        .invoke(LibraryClass(), x)

class ReflectionTestClass {
    fun test(bytes: ByteArray): Int {
        if (bytes.isEmpty()) return 1
        if (bytes.size % 2 == 0) {
            invoke(1)
        } else {
            invoke(2)
        }
        return 1
    }
}
