package ru.example.kotlinfuzzer.cli

import kotlinx.cli.*

private const val DELIMITER = ":"

class CommandLineArgs(parser: ArgParser) {
    val className by parser.option(ArgType.String, description = "Target class name").required()
    val methodName by parser.option(ArgType.String, description = "Target method name").required()
    val workingDirectory by parser.option(ArgType.String, description = "Working directory for corpus and crashes").required()
    val classpath by parser.option(ArgType.String, description = "Target ClassPath (delimited with colon)").required().delimiter(DELIMITER)
    val packages by parser.option(ArgType.String, description = "Target packages (delimited with colon)").required().delimiter(DELIMITER)

    private val threadsNumber by parser.option(ArgType.Int, description = "Number of threads for workers.")
        .default(Runtime.getRuntime().availableProcessors())

    fun threadsNumber() = threadsNumber.also {
        require(threadsNumber >= 1) { "Number of threads should be at least one." }
    }
}
