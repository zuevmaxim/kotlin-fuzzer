package kotlinx.fuzzer.cli

import kotlinx.cli.ArgParser
import kotlinx.fuzzer.fuzzing.Fuzzer

fun main(args: Array<String>) {
    try {
        val parser = ArgParser("kotlin-fuzzer")
        val arguments = CommandLineArgs(parser)
        parser.parse(args)

        val fuzzer = Fuzzer(arguments.toFuzzerArgs())
        fuzzer.start()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}
