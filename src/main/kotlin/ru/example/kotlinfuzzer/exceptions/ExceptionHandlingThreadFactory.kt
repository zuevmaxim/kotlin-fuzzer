package ru.example.kotlinfuzzer.exceptions

import ru.example.kotlinfuzzer.fuzzing.Fuzzer
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

class ExceptionHandlingThreadFactory(fuzzer: Fuzzer) : ThreadFactory {
    private val factory = Executors.defaultThreadFactory()
    private val exceptionHandler = ExceptionHandler(fuzzer)

    override fun newThread(runnable: Runnable): Thread = factory.newThread(runnable).also { thread ->
        thread.uncaughtExceptionHandler = exceptionHandler
    }

    class ExceptionHandler(private val fuzzer: Fuzzer) : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(p0: Thread?, exception: Throwable?) {
            fuzzer.stop(exception)
        }
    }
}
