package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.coverage.CoverageRunnerFactory
import kotlinx.fuzzer.fuzzing.Fuzzer
import kotlinx.fuzzer.fuzzing.FuzzerArgs
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.storage.ContextFactory
import kotlinx.fuzzer.fuzzing.storage.Storage

/** Set of services for independent worker execution. */
class FuzzerContext(
    val storage: Storage,
    arguments: FuzzerArgs,
    fuzzer: Fuzzer,
    contextFactory: ContextFactory
) {
    val coverageRunner = CoverageRunnerFactory.createCoverageRunner(arguments.classpath, arguments.packages)
    val targetMethod: TargetMethod
    val mutator = InputMutator(fuzzer, storage, contextFactory, 1)

    init {
        val className = arguments.className
        val targetClass = coverageRunner.loadClass(className) ?: error("Class $className not found.")
        targetMethod = TargetMethod(targetClass, arguments.methodName)
    }
}
