package ru.example.kotlinfuzzer.fuzzing

import ru.example.kotlinfuzzer.classload.Loader
import ru.example.kotlinfuzzer.cli.CommandLineArgs
import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import java.io.File

class Fuzzer(arguments: CommandLineArgs) {
    private val threadPool = PriorityThreadPool(1)
    private val methodRunner: MethodRunner
    private val targetMethod: TargetMethod
    private val storage: Storage

    init {
        val loader = Loader(arguments.classpath(), arguments.packages())
        val className = arguments.className
        methodRunner = MethodRunner { loader.load(it) }
        val targetClass = loader.classLoader().loadClass(className) ?: error("Class $className not found.")
        targetMethod = TargetMethod(targetClass, arguments.methodName)
        storage = Storage(targetMethod, methodRunner, File(arguments.workingDirectory))
    }

    fun start() {
        storage.listCorpusInput().map { InputRunner(this, storage, it) }.forEach { submit(it) }
    }

    fun submit(task: Runnable) {
        threadPool.execute(task)
    }
}
