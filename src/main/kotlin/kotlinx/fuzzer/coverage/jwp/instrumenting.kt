package kotlinx.fuzzer.coverage.jwp

import kotlinx.fuzzer.coverage.PackagesToCover
import net.bytebuddy.agent.ByteBuddyAgent
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

/** Transform all classes to instrument classes from [packages]. Does *not* reset previous coverage information. */
internal fun transform(packages: PackagesToCover) {
    JwpTransformer.packages = packages
    retransformLoadedClasses(JwpTransformer.instrumentation, packages)
}

/** This flag informs that some class was transformed in this thread. */
internal val classLoaded = JwpTransformer.classLoaded

private fun retransformLoadedClasses(instrumentation: Instrumentation, packages: PackagesToCover) {
    if (!instrumentation.isRetransformClassesSupported) return
    val classesToRetransform = instrumentation.allLoadedClasses
        .filter { packages.shouldBeCovered(it) }
        .filter { instrumentation.isModifiableClass(it) }
        .toTypedArray()
    if (classesToRetransform.isNotEmpty()) {
        // workaround for a bug: asserts when vararg is empty on JDK8
        // *** java.lang.instrument ASSERTION FAILED ***: "numClasses != 0" at JPLISAgent.c line: 1102
        // Exception in thread "main" java.lang.NullPointerException
        //	at sun.instrument.InstrumentationImpl.retransformClasses0(Native Method)
        //	at sun.instrument.InstrumentationImpl.retransformClasses(InstrumentationImpl.java:144)
        //	at kotlinx.fuzzer.coverage.jwp.InstrumentingKt.retransformLoadedClasses(instrumenting.kt:25)
        //	...
        instrumentation.retransformClasses(*classesToRetransform)
    }
}

private object JwpTransformer : ClassFileTransformer {
    var packages = PackagesToCover(emptyList())
    internal val classLoaded = ThreadLocal.withInitial { false }
    internal val instrumentation = ByteBuddyAgent.install()

    init {
        instrumentation.addTransformer(JwpTransformer, instrumentation.isRetransformClassesSupported)
    }

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
            ClassBranchAdapter.transform(classfileBuffer, loader)
                .also { classLoaded.set(true) }
        } catch (e: Throwable) {
            System.err.println("Failed to transform $className: $e")
            null
        }
    }
}
