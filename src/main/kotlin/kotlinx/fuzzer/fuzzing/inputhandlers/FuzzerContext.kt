package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.FuzzerArgs
import kotlinx.fuzzer.coverage.createCoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.storage.Storage

/** Set of services for worker execution. */
class FuzzerContext(
    val storage: Storage,
    arguments: FuzzerArgs,
    fuzzer: Fuzzer
) {
    val coverageRunner = createCoverageRunner(arguments.classpath, arguments.packages)
    val targetMethod: TargetMethod
    val mutator = InputMutator(fuzzer, storage, this, 1)
    val compositeCoverageCount = arguments.compositeCoverageCount

    init {
        val className = arguments.className
        val targetClass = coverageRunner.loadClass(className) ?: error("Class $className not found.")
        targetMethod = TargetMethod(targetClass, arguments.methodName)
    }
}
