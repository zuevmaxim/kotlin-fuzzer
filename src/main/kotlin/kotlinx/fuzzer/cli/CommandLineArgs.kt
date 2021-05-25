package kotlinx.fuzzer.cli

import kotlinx.cli.*
import kotlinx.fuzzer.Fuzz
import kotlinx.fuzzer.Fuzzer
import kotlinx.fuzzer.FuzzerArgs

private const val DELIMITER = ":"

class CommandLineArgs(parser: ArgParser) {
    val minimize by parser
        .option(ArgType.Boolean, description = "Corpus minimization takes minimum number of corpus inputs that have maximum coverage score. Use this to minimize number of unit tests.")
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
    private val threadsNumber by parser
        .option(ArgType.Int, description = "Number of threads for workers.")
        .default(Runtime.getRuntime().availableProcessors())
    private val corpusMemoryLimitMb by parser
        .option(ArgType.Int, description = "Corpus memory limit in Mb. Use it to control memory usage.")
        .default(Fuzzer.CORPUS_MEMORY_LIMIT_MB)
    private val saveCorpus by parser
        .option(ArgType.Boolean, description = "Flag to save corpus into \"corpus\" directory.")
        .default(Fuzzer.DEFAULT_SAVE_CORPUS)

    fun toFuzzerArgs() = FuzzerArgs(
        className,
        methodName,
        workingDirectory,
        classpath,
        packages,
        threadsNumber,
        corpusMemoryLimitMb = corpusMemoryLimitMb,
        saveCorpus = saveCorpus
    )
}
