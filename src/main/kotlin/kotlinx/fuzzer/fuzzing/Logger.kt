package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.input.Hash
import kotlinx.fuzzer.fuzzing.storage.Storage
import org.apache.commons.lang3.time.DurationFormatUtils
import java.io.File
import java.io.FileWriter
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt

class Logger(private val storage: Storage, private val stop: AtomicBoolean, workingDirectory: File, private val getTasksUsage: () -> Int) {
    private val startTime = time()
    private val out = File(workingDirectory, "log.txt")
        .apply { createNewFile() }
        .let { FileWriter(it, true) }
    private var lastFlushTime = startTime

    fun log(fail: FailInput, hash: Hash) = log("Fail found: $hash ${fail.e::class} ${fail.e.localizedMessage}")

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
                val tasksUsage = getTasksUsage()
                val memoryUsage = Runtime.getRuntime()
                    .let { 100 - it.freeMemory() * 100.0 / it.maxMemory() }
                    .let { "%.2f".format(it) }
                val corpusCount = storage.corpus.count()
                val crashCount = storage.crashes.count()
                val executedCount = storage.executed.count()
                val bestCoverage = (storage.bestCoverage.get().percent() * 100).roundToInt().toDouble() / 100
                clearLine()
                println("$runTime tasks queue: $tasksUsage%; mem: $memoryUsage% best coverage: $bestCoverage%; corpus: $corpusCount; crashes: $crashCount; executed: $executedCount")
            }
        } finally {
            flush()
            out.close()
        }
    }

    private fun time() = System.currentTimeMillis()

    private fun flush() {
        val currentTime = time()
        if (currentTime - lastFlushTime > FLUSH_TIMEOUT_MS) {
            lastFlushTime = currentTime
            out.flush()
        }
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
    }
}
