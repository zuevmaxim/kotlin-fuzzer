package kotlinx.fuzzer.classload

/** Loads classes from bytes definition. */
class MemoryClassLoader(parent: ClassLoader) : ClassLoader(parent) {

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
