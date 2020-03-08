package ru.example.kotlinfuzzer

class MemoryClassLoader : ClassLoader() {

    private val definitions = HashMap<String, ByteArray>()

    fun addDefinition(name: String, bytes: ByteArray) {
        definitions[name] = bytes
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        val bytes = definitions[name]
        return if (bytes != null) {
            defineClass(name, bytes, 0, bytes.size)
        } else super.loadClass(name, resolve)
    }

}
