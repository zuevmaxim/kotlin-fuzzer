package ru.example.kotlinfuzzer

import com.xenomachina.argparser.ArgParser

private const val DELIMITER = ':'

class CommandLineArgs(parser: ArgParser) {
    val classpath by parser.storing("Target ClassPath (delimited with colon)") { split(DELIMITER) }
    val packages by parser.storing("Target packages (delimited with colon)") { split(DELIMITER) }
    val className by parser.storing("Target class name")
    val methodName by parser.storing("Target method name")
}
