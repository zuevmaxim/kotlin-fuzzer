package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.storage.Storage
import org.apache.commons.lang3.time.DurationFormatUtils
import java.io.File
import java.io.FileWriter
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt

class Logger(
    private val storage: Storage,
    private val stop: AtomicBoolean,
    workingDirectory: File,
    private val fuzzer: Fuzzer
) : Runnable {
    private val startTime = time()
    private val logFile = File(workingDirectory, "log.txt").apply {
        createNewFile()
    }

    private val workerLogFile = ThreadLocal.withInitial {
        File.createTempFile("Worker", ".log", workingDirectory)
    }

    fun log(fail: FailInput) {
        val message = "Fail found: ${fail.hash} ${fail.e::class} ${fail.e.localizedMessage}"
        log(message)
        log(message, logFile)
    }

    fun log(message: String) = log(message, workerLogFile.get())

    private fun log(message: String, file: File) = FileWriter(file, true).use { out ->
        val runTime = format(time() - startTime)
        out.write("$runTime $message\n")
    }

    fun start() {
        Thread(this).apply {
            setUncaughtExceptionHandler { _, e -> fuzzer.stop(e) }
            start()
        }
    }

    override fun run() {
        while (!stop.get()) {
            try {
                Thread.sleep(LOG_TIMEOUT_MS)
            } catch (e: InterruptedException) {
            }
            val runTime = format(time() - startTime)
            val corpusCount = storage.corpusInputs.size
            val crashCount = storage.crashesCount
            val bestCoverage = (storage.bestCoverage.percent() * 100).roundToInt().toDouble() / 100
            print("\u001b[1A\u001b[2K")
            println("$runTime best coverage: $bestCoverage%; corpus: $corpusCount; crashes: $crashCount;")
        }
    }

    private fun time() = System.currentTimeMillis()

    private companion object {
        private const val LOG_TIMEOUT_MS = 3000L
        private const val FORMAT = "HH:mm:ss:SSS"
        private fun format(millis: Long) = DurationFormatUtils.formatDuration(millis, FORMAT, true)
    }
}
