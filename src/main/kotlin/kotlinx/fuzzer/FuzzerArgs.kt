package kotlinx.fuzzer

import kotlinx.fuzzer.fuzzing.storage.StorageStrategy

data class FuzzerArgs(
    val className: String,
    val methodName: String,
    val workingDirectory: String,
    val classpath: List<String> = emptyList(),
    private val _packages: List<String>,
    val threadsNumber: Int = Runtime.getRuntime().availableProcessors(),
    val storageStrategy: StorageStrategy? = null,
    val corpusMemoryLimitMb: Int = Fuzzer.CORPUS_MEMORY_LIMIT_MB,
    val saveCorpus: Boolean = Fuzzer.DEFAULT_SAVE_CORPUS
) {
    /** Class name is inserted into coverage list by default. */
    val packages = listOf(className, *_packages.toTypedArray())

    init {
        require(threadsNumber >= 1) { "Number of threads should be at least one." }
        require(corpusMemoryLimitMb >= 1) { "Corpus size should be at least one." }
    }
}
