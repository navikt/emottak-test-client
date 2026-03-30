package no.nav.emottak.test.client.application.ebxml.usecases

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.nav.emottak.test.client.adapters.ebxml.kafka.EbxmlKafkaProducer
import no.nav.emottak.test.client.application.ebxml.usecases.builders.EbxmlDocumentBuilder
import no.nav.emottak.test.client.domain.EbxmlResult
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

            val envelopeBytes = signedDoc.asByteArray()
            val attachmentBytes = builder.payload?.bytes

            val soapWithAttachments = SoapWithAttachments(
                envelope = envelopeBytes,
                attachment = attachmentBytes
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

@Serializable
data class SoapWithAttachments(
    val envelope: ByteArray,
    val attachment: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SoapWithAttachments

        if (!envelope.contentEquals(other.envelope)) return false
        if (!attachment.contentEquals(other.attachment)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = envelope.contentHashCode()
        result = 31 * result + (attachment?.contentHashCode() ?: 0)
        return result
    }
}
