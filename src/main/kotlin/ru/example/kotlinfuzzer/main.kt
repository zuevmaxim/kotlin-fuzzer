package ru.example.kotlinfuzzer

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import ru.example.kotlinfuzzer.classload.Loader

fun main(args: Array<String>) = mainBody {
    val arguments = ArgParser(args).parseInto(::CommandLineArgs)

    val loader = Loader(arguments.classpath, arguments.packages)

    val methodRunner = MethodRunner(loader.classLoader()) { loader.load(it) }
    val result = methodRunner.run(arguments.className, arguments.methodName)
    methodRunner.shutdown()
    println(result)
}
