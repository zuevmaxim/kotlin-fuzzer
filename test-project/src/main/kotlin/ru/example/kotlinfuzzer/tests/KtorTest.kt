package ru.example.kotlinfuzzer.tests

import io.ktor.http.cio.parseRequest
import io.ktor.http.cio.parseResponse
import io.ktor.util.InternalAPI
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import rawhttp.core.RawHttp

class Result<T>(code: () -> T) {
    var result: T? = null
        private set
    var exception: Throwable? = null
        private set

    init {
        try {
            result = code()
        } catch (t: Throwable) {
            exception = t
        }
    }

    fun fail() = exception != null || result == null
}

class KtorTest {
    @InternalAPI
    fun requestTest(bytes: ByteArray): Int {
        val result1 = Result { runBlocking { parseRequest(ByteReadChannel(bytes)) } }
        val result2 = Result { RawHttp().parseRequest(String(bytes)) }
        if (result1.fail()) {
            return -1
        }

        check(!result2.fail()) {
            val request = result1.result!!
            """
            Fail expected, but
            method=${request.method};uri={${request.uri}};headers=[${request.headers}]
            found. RawHTTP fails with exception ${result2.exception?.message} or null ${result2.result}
            """.trimIndent()
        }
        val method1 = result1.result!!.method.value
        val method2 = result2.result!!.method
        check(method2 == method1) { "Expected method equality, but $method1 and $method2 found" }
        val headers1 = result1.result!!.headers
        val headers2 = result2.result!!.headers
        for (header in headers2.asMap()) {
            val key = header.key
            val values = headers1.getAll(key).map { it.toString() }.toList()
            for (value in header.value) {
                check(values.contains(value)) { "($key, $value) header not found" }
            }
        }

        return 1
    }

    @InternalAPI
    fun responseTest(bytes: ByteArray): Int {
        val result1 = Result { runBlocking { parseResponse(ByteReadChannel(bytes)) } }
        val result2 = Result { RawHttp().parseResponse(String(bytes)) }
        if (result1.fail()) {
            return -1
        }

        check(!result2.fail()) {
            val response = result1.result!!
            """
            Fail expected, but
            status=${response.status};statusText={${response.statusText}};headers=[${response.headers}];varsion=${response.version}
            found. RawHTTP fails with exception ${result2.exception?.message} or null ${result2.result}
            """.trimIndent()
        }
        val status1 = result1.result!!.status
        val status2 = result2.result!!.statusCode
        check(status1 == status2) { "Expected status code equality, but $status1 and $status2 found" }
        val statusText1 = result1.result!!.statusText
        val statusText2 = result2.result!!.startLine.reason
        check(statusText1 == statusText2) { "Expected status text equality, but $statusText1 and $statusText2 found" }
        val headers1 = result1.result!!.headers
        val headers2 = result2.result!!.headers
        for (header in headers2.asMap()) {
            val key = header.key
            val values = headers1.getAll(key).map { it.toString() }.toList()
            for (value in header.value) {
                check(values.contains(value)) { "($key, $value) header not found" }
            }
        }

        return 1
    }
}

fun requestTest(http: String) {
    val request = runBlocking { parseRequest(ByteReadChannel(http)) }!!
    println("method=${request.method};version=${request.version};uri={${request.uri}};headers=[${request.headers}]")
}

fun responseTest(http: String) {
    val response = runBlocking { parseResponse(ByteReadChannel(http)) }!!
    println("status=${response.status};statusText={${response.statusText}};headers=[${response.headers}];version=${response.version}")
}
