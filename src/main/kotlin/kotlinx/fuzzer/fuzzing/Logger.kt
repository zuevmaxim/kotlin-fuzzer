package kotlinx.fuzzer.fuzzing

import org.apache.commons.lang3.time.DurationFormatUtils
import kotlinx.fuzzer.fuzzing.storage.Storage
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt

class Logger(private val storage: Storage, private val stop: AtomicBoolean) {
    private val startTime = time()

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
