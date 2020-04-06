package ru.example.kotlinfuzzer.cli

import kotlinx.cli.ArgParser
import ru.example.kotlinfuzzer.classload.Loader
import ru.example.kotlinfuzzer.coverage.MethodRunner

fun main(args: Array<String>) {
    val parser = ArgParser("kotlin-fuzzer")
    val arguments = CommandLineArgs(parser)
    parser.parse(args)

    val loader = Loader(arguments.classpath(), arguments.packages())

    val methodRunner = MethodRunner(loader.classLoader()) { loader.load(it) }
    val result = methodRunner.run(arguments.className, arguments.methodName)
    methodRunner.shutdown()
    println(result)
}
