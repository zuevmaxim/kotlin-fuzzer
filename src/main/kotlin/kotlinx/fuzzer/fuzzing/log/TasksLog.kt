package kotlinx.fuzzer.fuzzing.log

import java.util.concurrent.ForkJoinPool

/** Information about tasks execution. */
class TasksLog(private val threadPool: ForkJoinPool) {
    val queueSize: Int
        get() = threadPool.queuedSubmissionCount
}
