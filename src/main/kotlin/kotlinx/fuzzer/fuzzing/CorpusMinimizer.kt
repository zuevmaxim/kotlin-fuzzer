package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.coverage.createCoverageRunner
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.Hash
import kotlinx.fuzzer.fuzzing.input.Input
import kotlinx.fuzzer.fuzzing.log.Logger
import java.io.File
import java.io.IOException

/**
 * Find subset of corpus inputs with the same coverage.
 * Implements greedy solution of maximum coverage problem.
 */
class CorpusMinimizer(
    className: String,
    methodName: String,
    classpath: List<String> = emptyList(),
    packages: List<String>
) {
    private val targetMethod: TargetMethod
    private val coverageRunner = createCoverageRunner(classpath, packages)

    init {
        val targetClass = coverageRunner.loadClass(className) ?: error("Class $className not found.")
        targetMethod = TargetMethod(targetClass, methodName)
    }

    /** Create "minimized" subdirectory and put minimized corpus into it. */
    fun minimize(corpusDirectory: File) {
        val files = corpusDirectory.listFiles()?.filter { it.isFile }
            ?: throw IOException("Can not list files in directory ${corpusDirectory.absolutePath}")
        val outputDirectory = File(corpusDirectory, "minimized").apply { mkdir() }
        var size = 0
        val corpusInputs = mutableListOf<ExecutedInput>()
        var currentPriority = 0.0

        fun List<Input>.runInputs() = this
            .asSequence()
            .map { it.run(coverageRunner, targetMethod, corpusInputs) }
            .filterIsInstance<ExecutedInput>()
            .filter { it.userPriority > 0 }
            .onEach {
                val coverage = Logger.printFormat(it.priority())
                Logger.clearLine()
                Logger.info("run corpus: ${++size} of ${this.size}; coverage = $coverage")
            }
            .filter { it.priority() > currentPriority }
            .toList()

        var inputs = files
            .map { it.readBytes() }
            .map { Input(it) }
            .also {
                val total = Logger.printFormat(it[0].run(coverageRunner, targetMethod, it).priority())
                Logger.info("total coverage is $total\n")
            }
            .runInputs()

        var input = inputs.maxBy { it.priority() } ?: return
        while (input.priority() > currentPriority) {
            currentPriority = input.priority()
            corpusInputs.add(input)
            saveInput(input.minimize(coverageRunner, targetMethod), outputDirectory)
            size = 0
            inputs = inputs.filter { it !== input }.runInputs()
            input = inputs.maxBy { it.priority() } ?: return
        }
    }

    private fun saveInput(input: Input, directory: File) {
        val hash = Hash(input.data)
        val coverage = Logger.printFormat(input.priority())
        Logger.info("Add corpus input $hash; coverage = $coverage\n")
        File(directory, hash.toString()).apply {
            createNewFile()
            writeBytes(input.data)
        }
    }
}