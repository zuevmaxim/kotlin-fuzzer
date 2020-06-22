package kotlinx.fuzzer.coverage.jacoco.classload

/** Loads classes from bytes definition. */
internal class MemoryClassLoader(parent: ClassLoader) : ClassLoader(parent) {

    private val definitions = HashMap<String, ByteArray>()

    fun addDefinition(name: String, bytes: ByteArray) {
        definitions[name] = bytes
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        val bytes = definitions[name]
        return if (bytes != null) {
            defineClass(null, bytes, 0, bytes.size)
        } else {
            super.loadClass(name, resolve)
        }
    }

}
