package ru.example.kotlinfuzzer

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("3 arguments expected but ${args.size} found.")
        return
    }
    val (path, className, methodName) = args

    val methodRunner = MethodRunner(path, className)
    methodRunner.run(methodName)
}
