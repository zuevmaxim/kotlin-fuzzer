package kotlinx.fuzzer.coverage

object InstanceCreator {
    /** Create instance of class. Should have default public constructor. */
    fun constructDefault(clazz: Class<*>?): Any? {
        if (clazz == null) return null
        try {
            val constructor = clazz.getDeclaredConstructor()
            return constructor.newInstance()
        } catch (e: Throwable) {
            error("Cannot create instance of type ${clazz.simpleName}")
        }
    }
}
