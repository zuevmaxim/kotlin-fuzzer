package kotlinx.fuzzer

import kotlinx.fuzzer.fuzzing.storage.FilesStorageStrategy
import kotlinx.fuzzer.fuzzing.storage.StorageStrategy
import java.io.File

data class FuzzerArgs(
    val className: String,
    val methodName: String,
    val workingDirectory: String,
    val classpath: List<String> = emptyList(),
    val packages: List<String>,
    val maxTaskQueueSize: Int = Fuzzer.MAX_TASK_QUEUE_SIZE,
    val threadsNumber: Int = Runtime.getRuntime().availableProcessors(),
    val compositeCoverageCount: Int = 1,
    val storageStrategy: StorageStrategy = FilesStorageStrategy(File(workingDirectory))
) {
    init {
        require(threadsNumber >= 1) { "Number of threads should be at least one." }
        require(maxTaskQueueSize >= 1) { "Size of queue should be at least one." }
    }
}
