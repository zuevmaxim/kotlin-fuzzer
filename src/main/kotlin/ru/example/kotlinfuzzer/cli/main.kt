package ru.example.kotlinfuzzer.cli

import kotlinx.cli.ArgParser
import ru.example.kotlinfuzzer.fuzzing.Fuzzer

fun main(args: Array<String>) {
    val parser = ArgParser("kotlin-fuzzer")
    val arguments = CommandLineArgs(parser)
    parser.parse(args)

    val fuzzer = Fuzzer(arguments)
    fuzzer.start()
}
