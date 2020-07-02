package kotlinx.fuzzer.cli

import kotlinx.cli.*
import kotlinx.fuzzer.Fuzzer.Companion.MAX_TASK_QUEUE_SIZE
import kotlinx.fuzzer.*

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
        .option(ArgType.Int, description = "Maximum number of tasks in working queue. Use it to controll memory usage.")
        .default(MAX_TASK_QUEUE_SIZE)
    private val threadsNumber by parser
        .option(ArgType.Int, description = "Number of threads for workers.")
        .default(Runtime.getRuntime().availableProcessors())
    private val compositeCoverageCount by parser
        .option(ArgType.Int, description = "Number of corpus inputs runnning before new input. This allows cover several branches of code.")
        .default(1)

    fun toFuzzerArgs() = FuzzerArgs(
        className,
        methodName,
        workingDirectory,
        classpath,
        packages,
        maxTaskQueueSize,
        threadsNumber,
        compositeCoverageCount
    )
}
