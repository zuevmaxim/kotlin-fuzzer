package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.coverage.createCoverageRunner
import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.Hash
import kotlinx.fuzzer.fuzzing.input.Input
import kotlinx.fuzzer.fuzzing.log.Logger
import java.io.File
import java.io.IOException
import java.util.stream.Collectors

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
        var currentCoverage = 0.0

        fun List<Input>.runInputs() = this
            .parallelStream()
            .map { it.run(coverageRunner, targetMethod, corpusInputs) }
            .filter { it is ExecutedInput }
            .map { it as ExecutedInput }
            .filter { it.userPriority > 0 }
            .peek {
                val coverage = Logger.printFormat(it.coverage)
                Logger.infoClearLine("run corpus: ${++size} of ${this.size}; coverage = $coverage")
            }
            .filter { it.coverage > currentCoverage }
            .collect(Collectors.toList())

        var inputs = files
            .map { it.readBytes() }
            .map { Input(it) }
            .also { runAllInputs(it) }
            .runInputs()

        var input = inputs.maxBy { it.coverage } ?: return
        while (input.coverage > currentCoverage) {
            currentCoverage = input.coverage
            corpusInputs.add(input)
            saveInput(input.run(coverageRunner, targetMethod).minimize(coverageRunner, targetMethod), outputDirectory)
            size = 0
            inputs = inputs.filter { it !== input }.runInputs()
            input = inputs.maxBy { it.coverage } ?: return
        }
    }

    /** Execute all inputs to compute total coverage score. */
    private fun runAllInputs(inputs: List<Input>) {
        val executionResult = inputs[0].run(coverageRunner, targetMethod, preconditions = inputs)
        check(executionResult is ExecutedInput) { "Only success inputs expected." }
        val total = Logger.printFormat(executionResult.coverage)
        Logger.info("total coverage is $total\n")
    }

    private fun saveInput(input: Input, directory: File) {
        check(input is ExecutedInput)
        val hash = Hash(input.data)
        val coverage = Logger.printFormat(input.coverage)
        Logger.info("Add corpus input $hash; coverage = $coverage\n")
        File(directory, hash.toString()).apply {
            createNewFile()
            writeBytes(input.data)
        }
    }
}
