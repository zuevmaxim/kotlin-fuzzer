package ru.example.kotlinfuzzer

import ru.example.kotlinfuzzer.classload.Loader

private const val ARGUMENTS_NUMBER = 4

fun main(args: Array<String>) {
    if (args.size != ARGUMENTS_NUMBER) {
        println("$ARGUMENTS_NUMBER arguments expected but ${args.size} found.")
        println(
            """
            |1. classpath(delimiter=':')
            |2. list of packages to instrument(delimiter=':')
            |3. class name
            |4. method name to execute
        """.trimMargin()
        )
        return
    }
    val (path, packagesToInstrument, className, methodName) = args
    val classpath = path.split(":")
    val packages = packagesToInstrument.split(":")


    val loader = Loader(classpath, packages)

    val methodRunner = MethodRunner.Builder()
        .classes { loader.load(it) }
        .classLoader(loader.classLoader())
        .build()
    val result = methodRunner.run(className, methodName)
    methodRunner.shutdown()
    println(result)
}
