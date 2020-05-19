package kotlinx.fuzzer.fuzzing.inputhandlers

import kotlinx.fuzzer.classload.Loader
import kotlinx.fuzzer.coverage.MethodRunner
import kotlinx.fuzzer.fuzzing.Fuzzer
import kotlinx.fuzzer.fuzzing.FuzzerArgs
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.fuzzing.storage.ContextFactory
import kotlinx.fuzzer.fuzzing.storage.Storage

class Context(
    val storage: Storage,
    arguments: FuzzerArgs,
    fuzzer: Fuzzer,
    contextFactory: ContextFactory
) {
    val targetMethod: TargetMethod
    val methodRunner: MethodRunner
    val mutator = InputMutator(fuzzer, storage, contextFactory, 1)

    init {
        val loader = Loader(arguments.classpath, arguments.packages)
        val className = arguments.className
        methodRunner = MethodRunner { loader.load(it) }
        val targetClass = loader.classLoader().loadClass(className) ?: error("Class $className not found.")
        targetMethod = TargetMethod(targetClass, arguments.methodName)
    }
}
