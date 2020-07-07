package kotlinx.fuzzer.fuzzing.log

import kotlinx.fuzzer.fuzzing.input.Hash
import kotlinx.fuzzer.fuzzing.storage.Storage
import org.apache.commons.lang3.time.DurationFormatUtils
import java.io.File
import java.io.FileWriter
import java.util.concurrent.atomic.AtomicBoolean

class Logger(
    private val storage: Storage,
    private val stop: AtomicBoolean,
    workingDirectory: File,
    private val tasksLog: TasksLog
) {
    private val startTime = time()
    private val out = File(workingDirectory, "log.txt")
        .apply { createNewFile() }
        .let { FileWriter(it, true) }
    private var lastFlushTime = startTime

    fun log(e: Throwable, hash: Hash) = log("Fail found: $hash ${e::class} ${fileAndLineNumber(e)} ${e.localizedMessage}")

    fun log(message: String) {
        val runTime = format(time() - startTime)
        out.write("$runTime $message\n")
        flush()
    }

    fun run() {
        try {
            while (!stop.get()) {
                Thread.sleep(LOG_TIMEOUT_MS)
                val runTime = format(time() - startTime)
                val tasksUsage = tasksLog.queueUsage
                val memoryUsage = printFormat(memoryUsage())
                val corpusCount = storage.corpusCount
                val crashCount = storage.crashesCount
                val executedCount = tasksLog.completedTasks
                val bestCoverage = printFormat(storage.bestCoverage.get().score())
                clearLine()
                println("$runTime tasks queue: $tasksUsage%; mem: $memoryUsage% best coverage: $bestCoverage; corpus: $corpusCount; crashes: $crashCount; executed: $executedCount")
            }
        } finally {
            flush()
            out.close()
        }
    }

    private fun memoryUsage() = Runtime.getRuntime()
        .let { 100.0 - it.freeMemory() * 100.0 / it.maxMemory() }

    private fun time() = System.currentTimeMillis()

    private fun flush() {
        val currentTime = time()
        if (currentTime - lastFlushTime > FLUSH_TIMEOUT_MS) {
            lastFlushTime = currentTime
            out.flush()
        }
    }

    private fun fileAndLineNumber(e: Throwable): Pair<String?, Int?> {
        val elements = e.stackTrace
        if (elements == null || elements.isEmpty()) {
            return null to null
        }
        val stackElement = elements[0]
        return stackElement.fileName to stackElement.lineNumber
    }

    companion object {
        private const val FLUSH_TIMEOUT_MS = 1000L
        private const val LOG_TIMEOUT_MS = 3000L
        private const val FORMAT = "HH:mm:ss"
        private fun format(millis: Long) = DurationFormatUtils.formatDuration(millis, FORMAT, true)

        fun clearLine() {
            if (System.console() == null) return
            print("\u001b[1A\u001b[2K")
        }

        fun debug(message: String) = println("$message\n")

        fun info(message: String) = println(message)

        fun printFormat(value: Double): String = "%.2f".format(value)
    }
}
