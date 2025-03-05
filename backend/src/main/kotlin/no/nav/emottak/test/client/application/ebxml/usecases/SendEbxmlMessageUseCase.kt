package no.nav.emottak.test.client.application.ebxml.usecases

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import no.nav.emottak.test.client.application.ebxml.usecases.builders.EbxmlDocumentBuilder
import no.nav.emottak.test.client.domain.EbxmlResult
import no.nav.emottak.test.client.infrastructure.config.ApplicationConfig
import no.nav.emottak.test.client.infrastructure.xml.xmlMarshaller
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

@Serializable
data class EbxmlRequest(
    val fromPartyId: String,
    val fromRole: String,
    val toPartyId: String,
    val toRole: String,
    val cpaId: String,
    val conversationId: String = UUID.randomUUID().toString(),
    val service: String,
    val action: String,
    val messageId: String = UUID.randomUUID().toString(),
    val timestamp: String = Instant.now().toString(),
    val ebxmlPayload: EbxmlPayload? = null
)

@Serializable
data class EbxmlPayload(
    val base64Content: String,
    val contentId: String = "${UUID.randomUUID()}@emottak-test-payload.nav.no"
)

class SendEbxmlMessageUseCase(private val applicationConfig: ApplicationConfig, private val httpClient: HttpClient) {

    private val log = LoggerFactory.getLogger(SendEbxmlMessageUseCase::class.java)

    suspend fun sendEbxmlMessage(requestDto: EbxmlRequest): EbxmlResult {
        return try {
            validateRequestDto(requestDto)

            val builder = EbxmlDocumentBuilder(applicationConfig, requestDto)
            val signedDoc = builder.buildAndSign()
            val ebxmlXmlMessage = xmlMarshaller.documentToString(signedDoc)

            val soapMessageContentId = "<${UUID.randomUUID()}@emottak-test.nav.no>"

            val base64Payload = requestDto.ebxmlPayload?.base64Content
            val payloadContentId = "<${requestDto.ebxmlPayload?.contentId}>"

            val boundary = UUID.randomUUID().toString()

            val contentType = """
                multipart/related; type="text/xml"; boundary="$boundary"; start="$soapMessageContentId"; charset=utf-8
            """.trimIndent()

            return try {
                val partData = mutableListOf<PartData>(
                    PartData.FormItem(
                        ebxmlXmlMessage,
                        {},
                        Headers.build {
                            append("Content-Type", "text/xml; charset=UTF-8")
                            append("Content-Id", soapMessageContentId)
                            append("Content-Transfer-Encoding", "binary")
                        }
                    )
                )

                if (base64Payload != null) {
                    partData.add(
                        PartData.FormItem(
                            base64Payload,
                            {},
                            Headers.build {
                                append("Content-Type", "text/xml; charset=utf-8")
                                append("Content-Id", payloadContentId)
                                append("Content-Transfer-Encoding", "base64")
                            }
                        )
                    )
                }

                val url = applicationConfig.ebmsSyncRouterUrl

                val response = withContext(Dispatchers.IO) {
                    httpClient.post(url) {
                        headers {
                            append("Content-Type", contentType)
                            append("SOAPAction", "ebXML")
                            append("MIME-Version", "1.0")
                            append("X_SEND_TO", "ny")
                            append("Message-Id", requestDto.messageId)
                            append("Accept", "*/*")
                        }
                        setBody(
                            MultiPartFormDataContent(
                                partData,
                                boundary,
                                ContentType.parse(contentType)
                            )
                        )
                    }
                }

                val responseBody = response.bodyAsText()
                if (response.status == HttpStatusCode.OK) {
                    log.info("Successfully sent ebXML request")
                    EbxmlResult.Success(responseBody)
                } else {
                    log.error("Failed request with status: ${response.status}")
                    EbxmlResult.Failure(
                        "Unexpected status code: ${response.status} and response: ${response.bodyAsText()}",
                        response.status.value
                    )
                }
            } catch (e: Exception) {
                log.error("Error while sending ebXML request: ${e.message}", e)
                EbxmlResult.Failure("Exception: ${e.message}")
            }
        } catch (ex: IllegalArgumentException) {
            log.error("Validation failed: ${ex.message}", ex)
            EbxmlResult.Failure("Validation failed: ${ex.message}, 400")
        } catch (ex: Exception) {
            log.error("Unexpected error: ${ex.message}", ex)
            EbxmlResult.Failure("Unexpected error: ${ex.message}, 500")
        }
    }

    private fun validateRequestDto(requestDto: EbxmlRequest) {
        val errors = mutableListOf<String>()
        if (requestDto.messageId.isBlank()) errors.add("messageId cannot be blank")
        if (requestDto.conversationId.isBlank()) errors.add("conversationId cannot be blank")
        if (requestDto.fromPartyId.isBlank()) errors.add("fromPartyId cannot be blank")
        if (requestDto.fromRole.isBlank()) errors.add("fromRole cannot be blank")
        if (requestDto.toPartyId.isBlank()) errors.add("toPartyId cannot be blank")
        if (requestDto.toRole.isBlank()) errors.add("toRole cannot be blank")
        if (requestDto.cpaId.isBlank()) errors.add("cpaId cannot be blank")
        if (requestDto.service.isBlank()) errors.add("service cannot be blank")
        if (requestDto.action.isBlank()) errors.add("action cannot be blank")

        if (errors.isNotEmpty()) {
            throw IllegalArgumentException(errors.joinToString("; "))
        }
    }
}
