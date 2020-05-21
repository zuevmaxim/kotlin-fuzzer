package kotlinx.fuzzer.fuzzing.log

import java.util.concurrent.ThreadPoolExecutor

class TasksLog(private val threadPool: ThreadPoolExecutor, private val maxTaskQueueSize: Int) {
    fun queueUsage() = threadPool.queue.size * 100 / maxTaskQueueSize
    fun completedTasks() = threadPool.completedTaskCount
}
