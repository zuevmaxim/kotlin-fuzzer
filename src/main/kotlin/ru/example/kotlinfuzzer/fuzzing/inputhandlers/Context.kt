package ru.example.kotlinfuzzer.fuzzing.inputhandlers

import ru.example.kotlinfuzzer.classload.Loader
import ru.example.kotlinfuzzer.cli.CommandLineArgs
import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import ru.example.kotlinfuzzer.fuzzing.TargetMethod
import ru.example.kotlinfuzzer.fuzzing.storage.ContextFactory
import ru.example.kotlinfuzzer.fuzzing.storage.Storage

class Context(
    val storage: Storage,
    arguments: CommandLineArgs,
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
