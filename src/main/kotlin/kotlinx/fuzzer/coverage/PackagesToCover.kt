package kotlinx.fuzzer.coverage

internal class PackagesToCover(private val packages: Collection<String>) {
    fun shouldBeCovered(clazz: Class<*>) = shouldBeCovered(clazz.name)

    fun shouldBeCovered(className: String): Boolean = packages.any { className.isInPackage(it) }

    private fun String.isInPackage(packageName: String): Boolean {
        val name = this.replace('/', '.')
        return name.startsWith(packageName)
                && (name.length == packageName.length || name[packageName.length] == '.')
    }
}
