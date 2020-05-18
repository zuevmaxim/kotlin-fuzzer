package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.fuzzing.input.FailInput
import kotlinx.fuzzer.fuzzing.storage.Storage
import org.apache.commons.lang3.time.DurationFormatUtils
import java.io.File
import java.io.FileWriter
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt

class Logger(private val storage: Storage, private val stop: AtomicBoolean, workingDirectory: File) {
    private val startTime = time()
    private val logFile = File(workingDirectory, "log.txt").apply {
        createNewFile()
    }

    fun log(fail: FailInput) = log("Fail found: ${fail.hash} ${fail.e::class} ${fail.e.localizedMessage}")

    fun log(message: String) = FileWriter(logFile, true).use { out ->
        val runTime = format(time() - startTime)
        out.write("$runTime $message\n")
    }

    fun run() {
        while (!stop.get()) {
            Thread.sleep(LOG_TIMEOUT_MS)
            val runTime = format(time() - startTime)
            val corpusCount = storage.corpus.count()
            val crashCount = storage.crashes.count()
            val executedCount = storage.executed.count()
            val bestCoverage = (storage.bestCoverage.get().percent() * 100).roundToInt().toDouble() / 100
            print("\u001b[1A\u001b[2K")
            println("$runTime best coverage: $bestCoverage%; corpus: $corpusCount; crashes: $crashCount; executed: $executedCount")
        }
    }

    private fun time() = System.currentTimeMillis()

    private companion object {
        private const val LOG_TIMEOUT_MS = 3000L
        private const val FORMAT = "HH:mm:ss"
        private fun format(millis: Long) = DurationFormatUtils.formatDuration(millis, FORMAT, true)
    }
}
