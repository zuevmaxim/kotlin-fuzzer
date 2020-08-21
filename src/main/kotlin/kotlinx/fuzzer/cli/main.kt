package kotlinx.fuzzer.cli

import kotlinx.cli.ArgParser
import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.fuzzing.CorpusMinimizer
import java.io.File

fun main(args: Array<String>) {
    val parser = ArgParser("kotlin-fuzzer")
    val arguments = CommandLineArgs(parser)
    parser.parse(args)

    val fuzzArgs = arguments.toFuzzerArgs()
    if (arguments.minimize) {
        val corpusMinimizer =
            CorpusMinimizer(fuzzArgs.className, fuzzArgs.methodName, fuzzArgs.classpath, fuzzArgs.packages)
        corpusMinimizer.minimize(File(fuzzArgs.workingDirectory))
    } else {
        val fuzzer = Fuzzer(arguments.toFuzzerArgs())
        fuzzer.start()
    }
}
