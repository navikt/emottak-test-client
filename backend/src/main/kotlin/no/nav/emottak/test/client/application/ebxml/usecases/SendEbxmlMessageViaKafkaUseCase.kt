package no.nav.emottak.test.client.application.ebxml.usecases

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.nav.emottak.test.client.adapters.ebxml.kafka.EbxmlKafkaProducer
import no.nav.emottak.test.client.application.ebxml.usecases.builders.EbxmlDocumentBuilder
import no.nav.emottak.test.client.domain.EbxmlResult
import no.nav.emottak.test.client.domain.Payload
import no.nav.emottak.test.client.infrastructure.config.ApplicationConfig
import no.nav.emottak.test.client.infrastructure.xml.asByteArray
import org.slf4j.LoggerFactory
import java.util.UUID

class SendEbxmlMessageViaKafkaUseCase(
    private val applicationConfig: ApplicationConfig,
    private val kafkaProducer: EbxmlKafkaProducer
) {

    private val log = LoggerFactory.getLogger(SendEbxmlMessageViaKafkaUseCase::class.java)

    fun sendEbxmlMessageViaKafka(requestDto: EbxmlRequest): EbxmlResult {
        return try {
            val builder = EbxmlDocumentBuilder(applicationConfig, requestDto)
            val signedDoc = builder.buildAndSign()

            val soapWithAttachments = createSoapWithAttachments(
                envelope = signedDoc.asByteArray(),
                payload = builder.payload
            )

            val key = UUID.randomUUID().toString()
            val value = Json.encodeToString(soapWithAttachments).toByteArray()

            kafkaProducer.sendSoapWithAttachments(key, value)

            log.info("SoapWithAttachments message sent to Kafka with key: {}", key)
            EbxmlResult.Success("Message sent to Kafka with key: $key", "")
        } catch (e: Exception) {
            log.error("Error sending ebXML message via Kafka: {}", e.message, e)
            EbxmlResult.Failure("Failed to send via Kafka: ${e.message}")
        }
    }
}

internal fun createSoapWithAttachments(envelope: ByteArray, payload: Payload?): SoapWithAttachments = SoapWithAttachments(
    envelope = envelope,
    attachments = payload?.let {
        listOf(
            Attachment(
                content = it.bytes,
                contentId = it.contentId,
                contentType = it.contentType
            )
        )
    }.orEmpty()
)

@Serializable
data class SoapWithAttachments(
    val envelope: ByteArray,
    val attachments: List<Attachment> = emptyList()
) {
    companion object {
        const val MESSAGE_FORMAT_HEADER = "messageFormat"
        const val MESSAGE_FORMAT_VALUE = "SoapWithAttachments"
    }
}

@Serializable
data class Attachment(
    val content: ByteArray,
    val contentId: String,
    val contentType: String
)
