package no.nav.emottak.test.client.infrastructure.smtp

import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class SmtpTransportClient(
    private val baseUrl: String,
    private val httpClient: HttpClient,
    private val tokenProvider: suspend () -> String
) {

    private val log = LoggerFactory.getLogger(SmtpTransportClient::class.java)

    suspend fun storePayload(payloads: List<PayloadDto>) {
        val token = tokenProvider()
        val response = httpClient.post("$baseUrl/api/payloads") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(Json.encodeToString(payloads))
        }
        if (response.status != HttpStatusCode.Created) {
            val body = response.bodyAsText()
            log.error("Failed to store payload in smtp-transport: ${response.status} $body")
            throw RuntimeException("Failed to store payload: ${response.status} $body")
        }
        log.info("Stored ${payloads.size} payload(s) in smtp-transport")
    }
}

@Serializable
data class PayloadDto(
    val referenceId: String,
    val contentId: String,
    val contentType: String,
    val content: ByteArray
)
