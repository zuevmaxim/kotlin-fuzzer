package ru.example.kotlinfuzzer

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("3 arguments expected but ${args.size} found.")
        println(
            """
            |1. path to directory with .class file
            |2. class name
            |3. method name to execute
        """.trimMargin()
        )
        return
    }
    val (path, className, methodName) = args

    val methodRunner = MethodRunner(path, className)
    val result = methodRunner.run(methodName)
    printResult(System.out, result)
}
