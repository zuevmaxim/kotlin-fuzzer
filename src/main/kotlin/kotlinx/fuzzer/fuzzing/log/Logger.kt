package kotlinx.fuzzer.fuzzing.log

import kotlinx.fuzzer.fuzzing.input.Hash
import kotlinx.fuzzer.fuzzing.storage.Storage
import org.apache.commons.lang3.time.DurationFormatUtils
import java.io.File
import java.io.FileWriter
import java.util.concurrent.atomic.AtomicBoolean

class Logger(
    private val storage: Storage,
    /** A flag to stop execution. */
    private val stop: AtomicBoolean,
    workingDirectory: File,
    private val tasksLog: TasksLog
) {
    private val startTime = time()

    /** Log file. It is closed only at the end of execution. */
    private val out = File(workingDirectory, "log.txt")
        .apply { createNewFile() }
        .let { FileWriter(it, true) }
    private var lastFlushTime = startTime

    fun log(e: Throwable, hash: Hash) =
        log("Fail found: $hash ${e::class} ${fileAndLineNumber(e)} ${e.localizedMessage}")

    fun log(message: String) {
        val runTime = format(time() - startTime)
        out.write("$runTime $message\n")
        flush()
    }

    fun run() {
        try {
            while (!stop.get()) {
                flush()
                Thread.sleep(LOG_TIMEOUT_MS)
                val runTime = format(time() - startTime)
                val queueSize = tasksLog.queueSize
                val memoryUsage = printFormat(memoryUsage())
                val corpusCount = storage.corpusCount
                val crashCount = storage.crashesCount
                val bestCoverage = printFormat(storage.bestCoverage.get().score())
                clearLine()
                println("$runTime tasks queue: $queueSize; mem: $memoryUsage% best coverage: $bestCoverage; corpus: $corpusCount; crashes: $crashCount")
            }
        } finally {
            flush(force = true)
            out.close()
        }
    }

    private fun memoryUsage() = Runtime.getRuntime()
        .let { 100.0 - it.freeMemory() * 100.0 / it.maxMemory() }

    private fun time() = System.currentTimeMillis()

    private fun flush(force: Boolean = false) {
        val currentTime = time()
        if (force || currentTime - lastFlushTime > FLUSH_TIMEOUT_MS) {
            lastFlushTime = currentTime
            out.flush()
        }
    }

    /** Get filename and line number of first stacktrace element. */
    private fun fileAndLineNumber(e: Throwable): Pair<String?, Int?> {
        val elements = e.stackTrace
        if (elements == null || elements.isEmpty()) {
            return null to null
        }
        val stackElement = elements[0]
        return stackElement.fileName to stackElement.lineNumber
    }

    companion object {
        /** Clear line in console. */
        fun clearLine() {
            if (System.console() == null) return
            print(CLEAR_LINE_SEQUENCE)
        }

        /** Print debug message. */
        fun debug(message: String) = println("$message\n")

        /** Print info message. */
        fun info(message: String) = println(message)

        /** Print info message on current line in console. */
        fun infoClearLine(message: String) {
            if (System.console() == null) return info(message)
            println("$CLEAR_LINE_SEQUENCE$message")
        }

        /** Format double as two digit. */
        fun printFormat(value: Double): String = "%.2f".format(value)
    }
}

private const val FLUSH_TIMEOUT_MS = 1000L
private const val LOG_TIMEOUT_MS = 3000L
private const val FORMAT = "HH:mm:ss"
private const val CLEAR_LINE_SEQUENCE = "\u001b[1A\u001b[2K"
private fun format(millis: Long) = DurationFormatUtils.formatDuration(millis, FORMAT, true)
