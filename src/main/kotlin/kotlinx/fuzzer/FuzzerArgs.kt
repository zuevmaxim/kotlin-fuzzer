package kotlinx.fuzzer

import kotlinx.fuzzer.fuzzing.storage.StorageStrategy

data class FuzzerArgs(
    val className: String,
    val methodName: String,
    val workingDirectory: String,
    val classpath: List<String> = emptyList(),
    val packages: List<String>,
    val maxTaskQueueSize: Int = Fuzzer.MAX_TASK_QUEUE_SIZE,
    val threadsNumber: Int = Runtime.getRuntime().availableProcessors(),
    val storageStrategy: StorageStrategy? = null,
    val maxCorpusSize: Int = Fuzzer.MAX_CORPUS_SIZE,
    val saveCorpus: Boolean = Fuzzer.DEFAULT_SAVE_CORPUS
) {
    init {
        require(threadsNumber >= 1) { "Number of threads should be at least one." }
        require(maxTaskQueueSize >= 1) { "Size of queue should be at least one." }
        require(maxCorpusSize >= 1) { "Corpus size should be at least one." }
    }
}
