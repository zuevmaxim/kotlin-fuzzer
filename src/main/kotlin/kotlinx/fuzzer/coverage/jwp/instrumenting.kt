package kotlinx.fuzzer.coverage.jwp

import kotlinx.fuzzer.coverage.PackagesToCover
import net.bytebuddy.agent.ByteBuddyAgent
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

internal fun transform(packages: PackagesToCover, classLoaded: ThreadLocal<Boolean>) {
    val transformer = JwpTransformer(packages, classLoaded)
    val instrumentation = ByteBuddyAgent.install()
    instrumentation.addTransformer(transformer, instrumentation.isRetransformClassesSupported)
    retransformLoadedClasses(instrumentation, packages)
}

private fun retransformLoadedClasses(instrumentation: Instrumentation, packages: PackagesToCover) {
    if (!instrumentation.isRetransformClassesSupported) return
    val classesToRetransform = instrumentation.allLoadedClasses
        .filter { packages.shouldBeCovered(it) }
        .filter { instrumentation.isModifiableClass(it) }
        .toTypedArray()
    instrumentation.retransformClasses(*classesToRetransform)
}

private class JwpTransformer(
    private val packages: PackagesToCover,
    private val classLoaded: ThreadLocal<Boolean>
) : ClassFileTransformer {
    override fun transform(
        loader: ClassLoader?,
        className: String?,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray?
    ): ByteArray? {
        if (className == null || classfileBuffer == null || !packages.shouldBeCovered(className)) {
            return null
        }
        return try {
            ClassBranchAdapter.transform(classfileBuffer)
                .also { classLoaded.set(true) }
        } catch (e: Throwable) {
            System.err.println("Failed to transform $className: $e")
            null
        }
    }
}
