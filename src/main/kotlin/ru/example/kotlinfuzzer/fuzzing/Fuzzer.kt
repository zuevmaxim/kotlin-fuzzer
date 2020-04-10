package ru.example.kotlinfuzzer.fuzzing

import ru.example.kotlinfuzzer.classload.Loader
import ru.example.kotlinfuzzer.cli.CommandLineArgs
import ru.example.kotlinfuzzer.coverage.MethodRunner
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.HandlersNet
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.InputTask
import ru.example.kotlinfuzzer.fuzzing.inputhandlers.MutationTask
import ru.example.kotlinfuzzer.fuzzing.storage.Storage
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class Fuzzer(arguments: CommandLineArgs) {
    private val threadPool = Executors.newFixedThreadPool(1)  //PriorityThreadPool(1)
    private val methodRunner: MethodRunner
    private val targetMethod: TargetMethod
    private val storage: Storage
    private val handlersNet: HandlersNet

    val maxPriority: AtomicInteger

    init {
        val loader = Loader(arguments.classpath(), arguments.packages())
        val className = arguments.className
        methodRunner = MethodRunner { loader.load(it) }
        val targetClass = loader.classLoader().loadClass(className) ?: error("Class $className not found.")
        targetMethod = TargetMethod(targetClass, arguments.methodName)
        storage = Storage(targetMethod, methodRunner, File(arguments.workingDirectory))
        handlersNet = HandlersNet(this, storage)
        maxPriority = storage.bestPriority

        MutationTask(this, storage, handlersNet).start()
    }

    fun start() {
        storage.listCorpusInput().map { InputTask(handlersNet, it) }.forEach { submit(it) }
    }

    fun submit(task: Runnable) {
        threadPool.execute(task)
    }
}
