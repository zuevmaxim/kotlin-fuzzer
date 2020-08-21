package kotlinx.fuzzer.fuzzing.storage

import kotlinx.fuzzer.fuzzing.input.ExecutedInput
import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Hash
import java.io.File

class FilesStorageStrategy(workingDirectory: File, saveCorpus: Boolean) : StorageStrategy {
    private val corpus = if (saveCorpus) FileStorage(workingDirectory, "corpus") else null
    private val crashes = FileStorage(workingDirectory, "crashes")

    override fun save(input: ExecutedInput) = corpus?.save(input) ?: true
    override fun save(input: FailInput, hash: Hash) = crashes.save(input, hash)
}
