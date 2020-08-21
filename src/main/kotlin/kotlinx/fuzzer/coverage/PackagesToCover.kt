package kotlinx.fuzzer.coverage

/** Set of packages to cover. May include package names in format "a.b.c" or class names in format "a.b.c.D". */
internal class PackagesToCover(private val packages: Collection<String>) {
    fun shouldBeCovered(clazz: Class<*>) = shouldBeCovered(clazz.name)

    fun shouldBeCovered(className: String): Boolean = packages.any { className.isInPackage(it) }

    private fun String.isInPackage(packageName: String): Boolean {
        val name = this.replace('/', '.')
        return name.startsWith(packageName)
                && (name.length == packageName.length || name[packageName.length] == '.')
    }
}
