package ru.example.kotlinfuzzer

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import ru.example.kotlinfuzzer.classload.Loader

fun main(args: Array<String>) = mainBody {
    val arguments = ArgParser(args).parseInto(::CommandLineArgs)

    val loader = Loader(arguments.classpath, arguments.packages)

    val methodRunner = MethodRunner.Builder()
        .classes { loader.load(it) }
        .classLoader(loader.classLoader())
        .build()
    val result = methodRunner.run(arguments.className, arguments.methodName)
    methodRunner.shutdown()
    println(result)
}
