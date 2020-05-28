package kotlinx.fuzzer.tests.ktor.response

import io.ktor.http.cio.parseResponse
import io.ktor.util.InternalAPI
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.fuzzer.tests.ktor.Result
import rawhttp.core.RawHttp

class KtorResponseTest {

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

fun responseTest(http: String) {
    val response = runBlocking { parseResponse(ByteReadChannel(http)) }!!
    println("status=${response.status};statusText={${response.statusText}};headers=[${response.headers}];version=${response.version}")
}
