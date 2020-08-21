package kotlinx.fuzzer.cli

import kotlinx.cli.*
import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.Fuzzer.Companion.MAX_TASK_QUEUE_SIZE
import kotlinx.fuzzer.FuzzerArgs

private const val DELIMITER = ":"

class CommandLineArgs(parser: ArgParser) {
    val minimize by parser.option(ArgType.Boolean, description = "Corpus minimization takes minimum number of corpus inputs that have maximum coverage score. Use this to minimize number of unit tests.")
        .default(false)

    private val className by parser.option(ArgType.String, description = "Target class name").required()
    private val methodName by parser.option(ArgType.String, description = "Target method name").required()
    private val workingDirectory by parser
        .option(ArgType.String, description = "Working directory for corpus and crashes")
        .required()
    private val classpath by parser
        .option(ArgType.String, description = "Target ClassPath (delimited with colon)")
        .required()
        .delimiter(DELIMITER)
    private val packages by parser
        .option(ArgType.String, description = "Target packages (delimited with colon)")
        .required()
        .delimiter(DELIMITER)
    private val maxTaskQueueSize by parser
        .option(ArgType.Int, description = "Maximum number of tasks in working queue. Use it to control memory usage.")
        .default(MAX_TASK_QUEUE_SIZE)
    private val threadsNumber by parser
        .option(ArgType.Int, description = "Number of threads for workers.")
        .default(Runtime.getRuntime().availableProcessors())
    private val maxCorpusSize by parser
        .option(ArgType.Int, description = "Maximum size of internal corpus storage. Use it to control memory usage.")
        .default(Fuzzer.MAX_CORPUS_SIZE)

    fun toFuzzerArgs() = FuzzerArgs(
        className,
        methodName,
        workingDirectory,
        classpath,
        packages,
        maxTaskQueueSize,
        threadsNumber,
        maxCorpusSize = maxCorpusSize
    )
}
