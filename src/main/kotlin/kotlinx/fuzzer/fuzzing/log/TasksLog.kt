package kotlinx.fuzzer.fuzzing.log

import java.util.concurrent.ThreadPoolExecutor

class TasksLog(private val threadPool: ThreadPoolExecutor, private val maxTaskQueueSize: Int) {
    val queueUsage: Int
        get() = threadPool.queue.size * 100 / maxTaskQueueSize

    val completedTasks: Long
        get() = threadPool.completedTaskCount
}
