package ru.example.kotlinfuzzer.fuzzing

import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class PriorityThreadPool(nThreads: Int) {
    private val queue = PriorityBlockingQueue<Runnable>()
    private val threadPool = ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, queue)

    fun execute(task: Runnable) = threadPool.execute(task)
}
