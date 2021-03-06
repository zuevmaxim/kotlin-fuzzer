package ru.example.kotlinfuzzer

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

private const val DELIMITER = ':'

class CommandLineArgs(parser: ArgParser) {
    private val classpath by parser.option(ArgType.String, description = "Target ClassPath (delimited with colon)").required()
    private val packages by parser.option(ArgType.String, description = "Target packages (delimited with colon)").required()
    val className by parser.option(ArgType.String, description = "Target class name").required()
    val methodName by parser.option(ArgType.String, description = "Target method name").required()

    fun classpath() = classpath.split(DELIMITER)
    fun packages() = packages.split(DELIMITER)
}
