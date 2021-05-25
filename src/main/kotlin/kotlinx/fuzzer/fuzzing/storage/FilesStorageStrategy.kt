package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import java.io.File

/**
 * File storage for corpus and crashes.
 * @param saveCorpus a flag to persist corpus inputs
 */
class FilesStorageStrategy(workingDirectory: File, saveCorpus: Boolean) : StorageStrategy {
    private val corpus = if (saveCorpus) FileStorage(workingDirectory, "corpus") else null
    private val crashes = FileStorage(workingDirectory, "crashes")

    override fun save(input: ExecutedInput) = corpus?.save(input) ?: true
    override fun save(input: FailInput) = crashes.save(input)
}
